package factory;

import models.*;
import logic.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductFactory {
    public ProductFactory() {}

    public CommercialProduct createProduct(String rawName, String rawCategory, double rawPrice, String rawWeightString, nutritionalValues scrapedValues) {

        String cleanName = rawName.trim();

        double weight = parseWeight(rawWeightString);
        if (weight == 0.0) {
            weight = parseWeight(cleanName); // Fallback: cerca nel nome
        }

        // Se ancora 0, impostiamo un default standard (es. 100g) o scartiamo
        if (weight == 0.0) weight = 100.0;

        nutritionalValues finalValues = scrapedValues;
        boolean usedFallback = false;

        if (isNutritionMissing(scrapedValues)) {
            nutritionalValues fallback = FallbackNutritionService.getInstance().findByProductName(cleanName);

            if (fallback != null) {
                finalValues = fallback;
                usedFallback = true;
            } else {
                return null;
            }
        }

        appCategory mappedCategory = CategoryMapper.map(cleanName, rawCategory);

        if (mappedCategory == appCategory.SCONOSCIUTO) {
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


    private boolean isNutritionMissing(nutritionalValues v) {
        if (v == null) return true;
        return (v.getKcal() == 0 && v.getProteins() == 0 );
    }

    private double parseWeight(String text) {
        if (text == null || text.isEmpty()) return 0.0;

        // REGEX: Cerca un numero (anche con virgola) seguito da un'unità di misura
        // Gruppo 1: Il numero (es. "1,5")
        // Gruppo 2: L'unità (es. "Kg")
        // (?i) rende tutto case-insensitive (kg = KG = Kg)
        Pattern p = Pattern.compile("([0-9]+[.,]?[0-9]*)\\s*(mg|g|kg|ml|cl|l|lt)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);

        if (m.find()) {
            try {
                // Sostituiamo la virgola col punto per il parsing Java
                String numStr = m.group(1).replace(",", ".");
                double value = Double.parseDouble(numStr);
                String unit = m.group(2).toLowerCase();

                // Convertiamo tutto in GRAMMI / MILLILITRI
                switch (unit) {
                    case "kg":
                    case "l":
                    case "lt":
                        return value * 1000.0;
                    case "cl":
                        return value * 10.0;
                    case "mg":
                        return value / 1000.0;
                    default: // g, ml
                        return value;
                }
            } catch (NumberFormatException e) {
                return 0.0; // Se c'è un errore strano nel numero
            }
        }
        return 0.0; // Nessun peso trovato
    }

}
