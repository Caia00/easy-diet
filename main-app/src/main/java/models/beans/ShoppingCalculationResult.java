package models.beans;

import java.util.Map;

public class ShoppingCalculationResult {
    //Dati riferiti al singolo CommercialProduct
    private double totalGramsRequired;
    private int packsToBuy;

    //Mappa utilizzata per salvare quanti grammi di prodotto serviranno per ogni singola demand
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


}
