package logic;

import models.*;
import models.beans.MealDemand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietCalculatorService {
    private DietCalculatorService() {}

    /**
     * Metodo Principale: Riceve una lista di richieste (MealDemand) e un prodotto.
     * Restituisce il calcolo totale di quanto comprare.
     */
    public static ShoppingCalculationResult calculateShoppingNeeds(List<MealDemand> demandsToCover, CommercialProduct chosenProduct) {

        double totalGramsNeeded = 0.0;

        // Mappa per dire all'utente: "Lunedì Pranzo mangiane 100g, Martedì Cena 200g"
        Map<String, Double> gramsPerMealMap = new HashMap<>();

        // 1. ITERAZIONE: Calcoliamo la porzione specifica per ogni singolo pasto
        for (MealDemand demand : demandsToCover) {

            // Calcolo la porzione usando il target specifico di quel pasto
            double portionGrams = calculateSinglePortion(demand.getTarget(), chosenProduct);

            // Creo la chiave univoca (es. "Lunedì - Spuntino")
            String key = demand.getDayOfWeek() + " - " + demand.getMealName();

            gramsPerMealMap.put(key, portionGrams);
            totalGramsNeeded += portionGrams;
        }

        // 2. CALCOLO CONFEZIONI
        int packsToBuy = 0;
        double packWeight = chosenProduct.getWeightInGrams();

        if (packWeight > 0 && totalGramsNeeded > 0) {
            // Esempio: servono 350g, confezione 250g -> 1.4 -> ceil -> 2 confezioni
            packsToBuy = (int) Math.ceil(totalGramsNeeded / packWeight);
        }

        // Restituisco l'oggetto risultato
        return new ShoppingCalculationResult(totalGramsNeeded, packsToBuy, gramsPerMealMap);
    }

    /**
     * Metodo Logico Interno: Decide quale nutriente usare come riferimento ("Driver").
     */
    private static double calculateSinglePortion(NutritionalTarget target, CommercialProduct product) {
        if (product == null || target == null) return 0.0;

        appCategory cat = target.getCategory();
        double requiredAmount = 0.0;      // Numeratore (dal Target)
        double productAmountPer100 = 0.0; // Denominatore (dal Prodotto)

        switch (cat) {
            // --- GRUPPO PROTEINE ---
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

            // --- GRUPPO CARBOIDRATI COMPLESSI ---
            case PASTA:
            case RISO_E_CEREALI:
            case PANE_E_SOSTITUTI:
            case PATATE_E_TUBERI:
            case FARINE:
                requiredAmount = target.getTargetCarbs();
                productAmountPer100 = product.getCarbsPer100g();
                break;

            // --- GRUPPO ZUCCHERI E DOLCI (Logica Sugar introdotta) ---
            case DOLCI_E_SNACK:
            case BISCOTTI_E_DOLCI_COLAZIONE:
            case CEREALI_COLAZIONE:
            case BEVANDE:
            case CREME_SPALMABILI:
                // Priorità 1: ZUCCHERI
                if (target.getTargetSugar() > 0 && getSugar(product) > 0) {
                    requiredAmount = target.getTargetSugar();
                    productAmountPer100 = getSugar(product);
                }
                // Priorità 2: CARBOIDRATI
                else if (target.getTargetCarbs() > 0) {
                    requiredAmount = target.getTargetCarbs();
                    productAmountPer100 = product.getCarbsPer100g();
                }
                // Priorità 3: KCAL
                else {
                    requiredAmount = target.getTargetKcal();
                    productAmountPer100 = product.getKcalPer100g();
                }
                break;

            // --- GRUPPO GRASSI ---
            case OLIO_E_GRASSI:
            case FRUTTA_SECCA:
            case SALSE:
                requiredAmount = target.getTargetFats();
                productAmountPer100 = product.getFatsPer100g();
                break;

            // --- GRUPPO FIBRE E VITAMINE ---
            case VERDURA:
            case FRUTTA_FRESCA:
            case LEGUMI:
                // Priorità 1: FIBRE (Ottimo per verdure e legumi)
                if (target.getTargetFibers() > 0 && getFibers(product) > 0) {
                    requiredAmount = target.getTargetFibers();
                    productAmountPer100 = getFibers(product);
                }
                // Priorità 2: ZUCCHERI (Spesso usato per limitare la frutta)
                else if (target.getTargetSugar() > 0 && getSugar(product) > 0) {
                    requiredAmount = target.getTargetSugar();
                    productAmountPer100 = getSugar(product);
                }
                // Priorità 3: CARBOIDRATI
                else if (target.getTargetCarbs() > 0) {
                    requiredAmount = target.getTargetCarbs();
                    productAmountPer100 = product.getCarbsPer100g();
                }
                break;

            // --- DEFAULT / SCONOSCIUTO ---
            default:
                requiredAmount = target.getTargetKcal();
                productAmountPer100 = product.getKcalPer100g();
                break;
        }

        // --- CONTROLLO DI SICUREZZA (Anti-Divisione per Zero) ---
        // Se il prodotto non ha il nutriente richiesto (es. ho chiesto grassi a un finocchio),
        // provo a calcolare la porzione basandomi sulle CALORIE come ultima spiaggia.
        if (productAmountPer100 <= 0) {
            if (product.getKcalPer100g() > 0 && target.getTargetKcal() > 0) {
                return (target.getTargetKcal() / product.getKcalPer100g()) * 100.0;
            }
            return 0.0; // Impossibile calcolare
        }

        // FORMULA FINALE: (Target / ValoreSu100) * 100
        return (requiredAmount / productAmountPer100) * 100.0;
    }

    // --- Helper Methods per pulizia codice ---
    // Questi assumono che CommercialProduct implementi Edible o abbia accesso ai NutritionalValues

    private static double getSugar(CommercialProduct p) {
        // Accediamo all'oggetto NutritionalValues interno
        // Assicurati che CommercialProduct abbia un getter pubblico per nutritionalValues
        // oppure che deleghi questi metodi. Qui assumo l'accesso ai values.
        return p.getSugarPer100g();
    }

    private static double getFibers(CommercialProduct p) {
        return p.getFibersPer100g();
    }

}
