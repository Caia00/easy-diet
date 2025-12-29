package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class CarbStrategy implements PortionCalculationStrategy{
    @Override
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (product.getCarbsPer100g() > 0) {
            return (target.getTargetCarbs() / product.getCarbsPer100g()) * 100.0;
        }
        return 0.0;
    }
}
