package models.factory;

import models.*;
import models.services.CategoryMapper;
import models.services.FallbackNutritionService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductFactory {

    //Regex utilizzata per eliminare il camel case dal nome del prodotto: CarrefourPollo -> Carrefour Pollo, più facile ricerca peso e categoria successive
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");
    //Regex per la ricerca nel nome del prodotto di una stringa di tipo <num> <unità di misura>
    private static final Pattern WEIGHT_PATTERN = Pattern.compile("(\\d+[.,]?\\d*)\\s*(mg|g|kg|ml|cl|l|lt)", Pattern.CASE_INSENSITIVE);

    public ProductFactory() {
        //Costruttore vuoto in quanto l'oggetto non dovrà essere inizializzato
    }

    public CommercialProduct createProduct(String rawName, String rawCategory, double rawPrice, String rawWeightString, NutritionalValues scrapedValues) {

        if (rawName == null || rawName.trim().isEmpty()) {
            return null;
        }
        if (scrapedValues == null) {
            scrapedValues = new NutritionalValues();
        }

        String cleanName = normalizeName(rawName);

        double weight = parseWeight(rawWeightString);
        if (weight == 0.0) {
            weight = parseWeight(cleanName); // Fallback per cercare nel nome
        }

        if (weight == 0.0) weight = 100.0; //Imposta default se non si trova

        NutritionalValues finalValues = scrapedValues;
        boolean usedFallback = false;

        if (isNutritionMissing(scrapedValues)) {
            NutritionalValues fallback = FallbackNutritionService.getInstance().findByProductName(cleanName);

            if (fallback != null) {
                finalValues = fallback;
                usedFallback = true;
            } else {
                return null;
            }
        }

        AppCategory mappedCategory = CategoryMapper.map(cleanName, rawCategory);

        if (mappedCategory == AppCategory.SCONOSCIUTO) {
            return null;
        }

        return new CommercialProduct(
                cleanName,
                rawPrice,
                weight,
                mappedCategory,
                finalValues,
                usedFallback
        );
    }


    private boolean isNutritionMissing(NutritionalValues v) {
        if (v == null) return true;
        return (v.getKcal() == 0 && v.getProteins() == 0 );
    }

    private double parseWeight(String text) {
        if (text == null || text.isEmpty()) return 0.0;
        Matcher m = WEIGHT_PATTERN.matcher(text);

        if (m.find()) {
            try {
                // Sostituiamo la virgola col punto per il parsing Java
                String numStr = m.group(1).replace(",", ".");
                double value = Double.parseDouble(numStr);
                String unit = m.group(2).toLowerCase();

                // Conversione in grammi/millilitri
                switch (unit) {
                    case "kg","l","lt":
                        return value * 1000.0;
                    case "cl":
                        return value * 10.0;
                    case "mg":
                        return value / 1000.0;
                    default: // g, ml
                        return value;
                }
            } catch (NumberFormatException _) {
                return 0.0; // Se c'è un errore strano nel numero
            }
        }
        return 0.0; // Nessun peso trovato
    }

    private String normalizeName(String text) {
        if (text == null) return "";

        String result = text.trim();
        result = CAMEL_CASE_PATTERN.matcher(result).replaceAll(" ");
        result = result.replace("_", " ").replace("-", " ");
        result = result.replaceAll("\\s+", " "); //Rimuove doppi spazi creati

        return result;
    }

}
