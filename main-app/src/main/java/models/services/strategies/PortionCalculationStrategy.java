package models.services.strategies;

import models.CommercialProduct;
import models.NutritionalTarget;

public interface PortionCalculationStrategy {
    double calculate(NutritionalTarget target, CommercialProduct product);
}
