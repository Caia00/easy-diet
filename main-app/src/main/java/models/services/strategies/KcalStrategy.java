package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class KcalStrategy implements PortionCalculationStrategy{
    @Override
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (product.getKcalPer100g() > 0) {
            return (target.getTargetKcal() / product.getKcalPer100g()) * 100.0;
        }
        return 0.0;
    }
}
