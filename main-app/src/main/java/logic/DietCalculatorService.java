package logic;

import models.*;
import models.beans.MealDemand;
import models.beans.ShoppingCalculationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietCalculatorService {
    private DietCalculatorService() {}

    //Metodo che passati una lista di richieste alimentari e un determinato prodotto determinerà quanto mangiarne e quanto comprarne
    public static ShoppingCalculationResult calculateShoppingNeeds(List<MealDemand> demandsToCover, CommercialProduct chosenProduct) {

        double totalGramsNeeded = 0.0;

        // Mappa utilizzata per salvare per ogni determinato pasto quanto prodotto si mangerà
        Map<String, Double> gramsPerMealMap = new HashMap<>();

        // Iterazione sulle varie MealDemand per riempire la gramsPerMealMap
        for (MealDemand demand : demandsToCover) {

            // Calcolo la porzione del prodotto usando il target specifico di quel pasto
            double portionGrams = calculateSinglePortion(demand.getTarget(), chosenProduct);

            // Creo la chiave univoca per la mappa
            String key = demand.getDayOfWeek() + " - " + demand.getMealName();

            gramsPerMealMap.put(key, portionGrams);
            totalGramsNeeded += portionGrams;
        }

        // Calcolo quante confezioni di prodotto selezionato acquistare
        int packsToBuy = 0;
        double packWeight = chosenProduct.getWeightInGrams();

        if (packWeight > 0 && totalGramsNeeded > 0) {
            packsToBuy = (int) Math.ceil(totalGramsNeeded / packWeight);
        }

        return new ShoppingCalculationResult(totalGramsNeeded, packsToBuy, gramsPerMealMap);
    }

    //Metodo interno usato per calcolare la porzione del prodotto usando determinati valori del target scelti con logica
    private static double calculateSinglePortion(NutritionalTarget target, CommercialProduct product) {
        if (product == null || target == null) return 0.0;

        appCategory cat = target.getCategory();
        double requiredAmount = 0.0;
        double productAmountPer100 = 0.0;

        switch (cat) {
            // PROTEINE
            case CARNE_BIANCA:
            case CARNE_ROSSA:
            case PESCE:
            case UOVA:
            case AFFETTATI_E_SALUMI:
            case LATTE_E_BEVANDE_VEG:
            case YOGURT_E_FERMENTATI:
            case FORMAGGI:
                requiredAmount = target.getTargetProteins();
                productAmountPer100 = product.getProteinsPer100g();
                break;

            // CARBOIDRATI
            case PASTA:
            case RISO_E_CEREALI:
            case PANE_E_SOSTITUTI:
            case PATATE_E_TUBERI:
            case FARINE:
                requiredAmount = target.getTargetCarbs();
                productAmountPer100 = product.getCarbsPer100g();
                break;

            // ZUCCHERI E DOLCI
            case DOLCI_E_SNACK:
            case BISCOTTI_E_DOLCI_COLAZIONE:
            case CEREALI_COLAZIONE:
            case BEVANDE:
            case CREME_SPALMABILI:
                // Priorità 1: ZUCCHERI
                if (target.getTargetSugar() > 0 && product.getSugarPer100g() > 0) {
                    requiredAmount = target.getTargetSugar();
                    productAmountPer100 = product.getSugarPer100g();
                }
                else {
                    requiredAmount = target.getTargetKcal();
                    productAmountPer100 = product.getKcalPer100g();
                }
                break;

            // GRASSI
            case OLIO_E_GRASSI:
            case FRUTTA_SECCA:
            case SALSE:
                requiredAmount = target.getTargetFats();
                productAmountPer100 = product.getFatsPer100g();
                break;

            // FIBRE
            case VERDURA:
            case FRUTTA_FRESCA:
            case LEGUMI:
                // Priorità 1: FIBRE
                if (target.getTargetFibers() > 0 && product.getFibersPer100g() > 0) {
                    requiredAmount = target.getTargetFibers();
                    productAmountPer100 = product.getFibersPer100g();
                }
                //Priorità 2: CARBOIDRATI
                else if (target.getTargetCarbs() > 0) {
                    requiredAmount = target.getTargetCarbs();
                    productAmountPer100 = product.getCarbsPer100g();
                }
                // Priorità 3: KCAL
                else if (target.getTargetKcal() > 0 && product.getKcalPer100g() > 0) {
                    requiredAmount = target.getTargetSugar();
                    productAmountPer100 = product.getKcalPer100g();
                }

                break;

            default:
                requiredAmount = target.getTargetKcal();
                productAmountPer100 = product.getKcalPer100g();
                break;
        }

        //Controllo finale, se nutriente richiesto assente calcolo porzione basandomi su KCAL
        if (productAmountPer100 <= 0) {
            if (product.getKcalPer100g() > 0 && target.getTargetKcal() > 0) {
                return (target.getTargetKcal() / product.getKcalPer100g()) * 100.0;
            }
            return 0.0; // Impossibile calcolare
        }

        return (requiredAmount / productAmountPer100) * 100.0;
    }

}
