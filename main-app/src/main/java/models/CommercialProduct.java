package models;

public class CommercialProduct implements Edible{
    private String name;
    private double price;
    private double weightInGrams;
    private AppCategory category;
    private NutritionalValues nutritionValues;
    private boolean isDataEstimated; // Flag utilizzata per specificare se valori presi da catalogo o da fallback db

    public CommercialProduct(String name, double price, double weightInGrams, AppCategory category, NutritionalValues nutritionValues, boolean isDataEstimated) {
        this.name = name;
        this.price = price;
        this.weightInGrams = weightInGrams;
        this.category = category;
        this.nutritionValues = nutritionValues;
        this.isDataEstimated = isDataEstimated;
    }

    @Override
    public double getKcalPer100g() { return nutritionValues.getKcal(); }

    @Override
    public double getCarbsPer100g() { return nutritionValues.getCarbs(); }

    @Override
    public double getSugarPer100g() { return nutritionValues.getSugar(); }

    @Override
    public double getFatsPer100g() { return nutritionValues.getFats(); }

    @Override
    public double getFibersPer100g() { return nutritionValues.getFibers(); }

    @Override
    public double getProteinsPer100g() { return nutritionValues.getProteins(); }

    @Override
    public String getCategory() { return category.name(); }


    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getWeightInGrams() { return weightInGrams; }
    public boolean isDataEstimated() { return isDataEstimated; }
    public NutritionalValues getNutritionalValues() { return nutritionValues; }

    public double getTotalKcal() {
        return (nutritionValues.getKcal() * weightInGrams) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%1.f) - €%.2f %s",
                name, weightInGrams, price, nutritionValues.toString());
    }

}
