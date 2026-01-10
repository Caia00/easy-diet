package models.services;

import models.*;
import models.beans.MealDemand;
import models.beans.ShoppingCalculationResult;
import models.services.strategies.KcalStrategy;
import models.services.strategies.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietCalculatorService {

    private static final Map<AppCategory, PortionCalculationStrategy> strategyMap = new EnumMap<>(AppCategory.class);
    private static final PortionCalculationStrategy defaultStrategy = new KcalStrategy();

    //Configurazione mappa delle strategie
    static {
        //ProteinStrategy
        PortionCalculationStrategy protein = new ProteinStrategy();
        strategyMap.put(AppCategory.CARNE_BIANCA, protein);
        strategyMap.put(AppCategory.CARNE_ROSSA, protein);
        strategyMap.put(AppCategory.PESCE, protein);
        strategyMap.put(AppCategory.AFFETTATI_E_SALUMI, protein);
        strategyMap.put(AppCategory.UOVA, protein);
        strategyMap.put(AppCategory.LATTE_E_BEVANDE_VEG, protein);
        strategyMap.put(AppCategory.LEGUMI, protein);


        //CarbStrategy
        PortionCalculationStrategy carb = new CarbStrategy();
        strategyMap.put(AppCategory.PASTA, carb);
        strategyMap.put(AppCategory.RISO_E_CEREALI, carb);
        strategyMap.put(AppCategory.PANE_E_SOSTITUTI, carb);
        strategyMap.put(AppCategory.PATATE_E_TUBERI, carb);
        strategyMap.put(AppCategory.FARINE, carb);

        //FatStrategy
        PortionCalculationStrategy fat = new FatStrategy();
        strategyMap.put(AppCategory.FRUTTA_SECCA, fat);
        strategyMap.put(AppCategory.YOGURT_E_FERMENTATI, fat);
        strategyMap.put(AppCategory.FORMAGGI, fat);
        strategyMap.put(AppCategory.OLIO_E_GRASSI, fat);
        strategyMap.put(AppCategory.SALSE, fat);

        //SugarPriorityStrategy
        PortionCalculationStrategy sugarPriority = new SugarPriorityStrategy();
        strategyMap.put(AppCategory.BISCOTTI_E_DOLCI_COLAZIONE, sugarPriority);
        strategyMap.put(AppCategory.CEREALI_COLAZIONE, sugarPriority);
        strategyMap.put(AppCategory.DOLCI_E_SNACK, sugarPriority);
        strategyMap.put(AppCategory.CREME_SPALMABILI, sugarPriority);
        strategyMap.put(AppCategory.BEVANDE, sugarPriority);

        //FiberPriorityStrategy
        PortionCalculationStrategy fiberPriority = new FiberPriorityStrategy();
        strategyMap.put(AppCategory.VERDURA, fiberPriority);
        strategyMap.put(AppCategory.FRUTTA_FRESCA, fiberPriority);
    }

    private DietCalculatorService() {}

    public static ShoppingCalculationResult calculateShoppingNeeds(List<MealDemand> demandsToCover, CommercialProduct chosenProduct) {

        double totalGramsNeeded = 0.0;
        Map<String, Double> gramsPerMealMap = new HashMap<>();

        for (MealDemand demand : demandsToCover) {
            double portionGrams = calculatePortionWithStrategy(demand.getTarget(), chosenProduct);

            String key = demand.getDayOfWeek() + " - " + demand.getMealName();

            gramsPerMealMap.put(key, portionGrams);
            totalGramsNeeded += portionGrams;
        }

        int packsToBuy = 0;
        double packWeight = chosenProduct.getWeightInGrams();

        if (packWeight > 0 && totalGramsNeeded > 0) {
            packsToBuy = (int) Math.ceil(totalGramsNeeded / packWeight);
        }

        return new ShoppingCalculationResult(totalGramsNeeded, packsToBuy, gramsPerMealMap);
    }

    //Metodo helper privato che calcola la porzione utilizzando la strategia appropriata
    private static double calculatePortionWithStrategy(NutritionalTarget target, CommercialProduct product) {
        if (product == null || target == null) return 0.0;

        PortionCalculationStrategy strategy = strategyMap.get(target.getCategory());

        //Se la categoria non è mappata uso il default
        if (strategy == null) {
            strategy = defaultStrategy;
        }

        double result = strategy.calculate(target, product);

        //Fallback, se la strategia specifica non è riuscita a calcolare uso quella di default
        if (result <= 0.0) {
            result = defaultStrategy.calculate(target, product);
        }

        return result;
    }

}
