package models.services;

import models.CommercialProduct;
import models.NutritionalValues;
import models.factory.ProductFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.*;

public class CsvCatalogLoader implements CatalogLoader {
    private static final Logger logger = Logger.getLogger(CsvCatalogLoader.class.getName());
    private final String fileName;
    private final ProductFactory factory;

    // Pattern precompilato per la ricerca di un numero all'interno di una stringa (utilizzato nel parser)
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+([.,]\\d+)?)");

    //Pattern precompilato per la ricerca di un numero seguito da kcal
    private static final Pattern KCAL_SPECIFIC_PATTERN = Pattern.compile("(\\d++(?:[.,]\\d++)?)\\s*+k?cal", Pattern.CASE_INSENSITIVE);

    public CsvCatalogLoader(String fileName, ProductFactory factory) {
        this.fileName = fileName;
        this.factory = factory;
    }

    @Override
    public List<CommercialProduct> loadCatalog() {
        List<CommercialProduct> catalog = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/" + fileName);
             BufferedReader br = (is != null) ?
                     new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)) : null) {

            if (br == null) {
                logger.severe(() -> "File non trovato: " + fileName);
                return catalog;
            }

            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                CommercialProduct product = parseLine(line);
                if (product != null) catalog.add(product);
            }

            logger.info(() -> "Caricati " + catalog.size() + " prodotti da " + fileName);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Il catalogo non è stato correttamente importato", e);
        }
        return catalog;
    }

    //Metodo che presa la stringa dal file csv acquisisce da essa tutti i valori di cui ha bisogno per creare un CommercialProduct
    private CommercialProduct parseLine(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length < 4) return null;

            //Riempimento dati prodotto
            String rawName = parts[0];
            String rawCategory = parts[1];
            double rawPrice = parseDoubleSafe(parts[2]);
            String rawWeightString = parts[3];

            //Creazione e riempimento delle NutritionalValues
            NutritionalValues scrapedValues = new NutritionalValues(
                    parseKcal(safeGet(parts, 4)),
                    parseDoubleSafe(safeGet(parts, 5)),
                    parseDoubleSafe(safeGet(parts, 6)),
                    parseDoubleSafe(safeGet(parts, 7)),
                    parseDoubleSafe(safeGet(parts, 8)),
                    parseDoubleSafe(safeGet(parts, 9))
            );

            return factory.createProduct(rawName, rawCategory, rawPrice, rawWeightString, scrapedValues);

        } catch (Exception _) {
            return null;
        }
    }

    //Metodo parser utilizzato per estrarre valori numerici da stringhe che potrebbero essere in formati non prevedibili
    private double parseDoubleSafe(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;

        Matcher matcher = NUMBER_PATTERN.matcher(text);

        if (matcher.find()) {
            try {
                String numberPart = matcher.group(1);
                numberPart = numberPart.replace(",", ".");

                return Double.parseDouble(numberPart);
            } catch (NumberFormatException _) {
                return 0.0;
            }
        }

        //Se non vengono trovati numeri nella stringa si considera il valore assente quindi 0
        return 0.0;
    }

    //Metodo parser specifico per le Kcal che potrebbero trovarsi in formati complessi come la presenza di due valori numeri uno per Kj e uno per Kcal
    private double parseKcal(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;

        Matcher matcher = KCAL_SPECIFIC_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                String numberPart = matcher.group(1).replace(",", ".");
                return Double.parseDouble(numberPart);
            } catch (NumberFormatException _) {
                //Se non si dovesse trovare il numero si prosegue col parser generico
            }
        }

        return parseDoubleSafe(text);
    }

    //Metodo per evitare errori se nel catalogo alcuni prodotti sono salvati con meno campi del previsto
    private String safeGet(String[] parts, int index) {
        if (index < parts.length) return parts[index];
        return "";
    }
}