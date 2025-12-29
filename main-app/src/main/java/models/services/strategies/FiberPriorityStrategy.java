package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public class FiberPriorityStrategy implements PortionCalculationStrategy{
    public double calculate(NutritionalTarget target, CommercialProduct product) {
        if (target.getTargetFibers() > 0 && product.getFibersPer100g() > 0) {
            return (target.getTargetFibers() / product.getFibersPer100g()) * 100.0;
        }
        if (target.getTargetCarbs() > 0 && product.getCarbsPer100g() > 0) {
            return (target.getTargetCarbs() / product.getCarbsPer100g()) * 100.0;
        }
        if (product.getKcalPer100g() > 0) {
            return (target.getTargetKcal() / product.getKcalPer100g()) * 100.0;
        }
        return 0.0;
    }
}
