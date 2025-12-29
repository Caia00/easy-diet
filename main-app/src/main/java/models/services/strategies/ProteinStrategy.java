package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class ProteinStrategy implements PortionCalculationStrategy{
    @Override
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (product.getProteinsPer100g() > 0) {
            return (target.getTargetProteins() / product.getProteinsPer100g()) * 100.0;
        }
        return 0.0;
    }
}
