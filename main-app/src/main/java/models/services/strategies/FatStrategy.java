package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class FatStrategy implements PortionCalculationStrategy{
    @Override
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (product.getFatsPer100g() > 0) {
            return (target.getTargetFats() / product.getFatsPer100g()) * 100.0;
        }
        return 0.0;
    }
}
