package factory;

import models.*;
import logic.*;

public class ProductFactory {
    public ProductFactory() {}

    public CommercialProduct createProduct(String rawName, String rawCategory, double rawPrice, nutritionalValues scrapedValues) {

        String cleanName = rawName.trim();
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
                mappedCategory,
                finalValues,
                usedFallback
        );
    }


    private boolean isNutritionMissing(nutritionalValues v) {
        if (v == null) return true;
        return (v.getKcal() == 0 && v.getProteins() == 0 );
    }

}
