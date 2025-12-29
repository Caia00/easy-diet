package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class SugarPriorityStrategy implements PortionCalculationStrategy{
    @Override
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (target.getTargetSugar() > 0 && product.getSugarPer100g() > 0) {
            return (target.getTargetSugar() / product.getSugarPer100g()) * 100.0;
        }
        if (product.getKcalPer100g() > 0) {
            return (target.getTargetKcal() / product.getKcalPer100g()) * 100.0;
        }
        return 0.0;
    }
}
