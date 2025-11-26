package models.beans;

import java.util.Map;

public class ShoppingCalculationResult {
    // Dati utilizzati nella lista della spesa
    private double totalGramsRequired;
    private int packsToBuy;

    // Dati in dettaglio visionati dall'utente quando osserverà il meal plan
    private Map<String, Double> gramsPerMealDetail;

    public ShoppingCalculationResult(double totalGramsRequired, int packsToBuy, Map<String, Double> gramsPerMealDetail) {
        this.totalGramsRequired = totalGramsRequired;
        this.packsToBuy = packsToBuy;
        this.gramsPerMealDetail = gramsPerMealDetail;
    }

    public double getTotalGramsRequired() {
        return totalGramsRequired;
    }

    public int getPacksToBuy() {
        return packsToBuy;
    }

    public Map<String, Double> getGramsPerMealDetail() {
        return gramsPerMealDetail;
    }

    public double calculateLeftovers(double singlePackWeight) {
        double totalBought = packsToBuy * singlePackWeight;
        return totalBought - totalGramsRequired;
    }

    // --- TOSTRING (Per il debug in console) ---
    @Override
    public String toString() {
        return "Risultato Spesa: " +
                "Servono " + String.format("%.1f", totalGramsRequired) + "g " +
                "-> Comprare " + packsToBuy + " confezioni. " +
                "(Dettagli pasti salvati: " + gramsPerMealDetail.size() + ")";
    }
}
