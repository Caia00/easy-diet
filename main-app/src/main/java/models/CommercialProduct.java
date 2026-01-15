package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommercialProduct implements Edible{
    private String name;
    private double price;
    private double weightInGrams;
    private AppCategory category;
    private NutritionalValues nutritionValues = new NutritionalValues();
    private boolean isDataEstimated; // Flag utilizzata per specificare se valori presi da catalogo o da fallback db

    public CommercialProduct(){}

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

    public void setKcalPer100g(double kcalPer100g) {
        nutritionValues.setKcal(kcalPer100g);
    }

    @Override
    public double getCarbsPer100g() { return nutritionValues.getCarbs(); }

    public void setCarbsPer100g(double carbsPer100g) {
        nutritionValues.setCarbs(carbsPer100g);
    }

    @Override
    public double getSugarPer100g() { return nutritionValues.getSugar(); }

    public void setSugarPer100g(double sugarPer100g) {
        nutritionValues.setSugar(sugarPer100g);
    }

    @Override
    public double getFatsPer100g() { return nutritionValues.getFats(); }

    public void setFatsPer100g(double fatsPer100g) {
        nutritionValues.setFats(fatsPer100g);
    }

    @Override
    public double getFibersPer100g() { return nutritionValues.getFibers(); }

    public void setFibersPer100g(double fibersPer100g) {
        nutritionValues.setFibers(fibersPer100g);
    }

    @Override
    public double getProteinsPer100g() { return nutritionValues.getProteins(); }

    public void setProteinsPer100g(double proteinsPer100g) {
        nutritionValues.setProteins(proteinsPer100g);
    }

    @Override
    public AppCategory getCategory() { return category; }

    public void setCategory(AppCategory category) { this.category = category; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getWeightInGrams() { return weightInGrams; }
    public void setWeightInGrams(double weightInGrams) {this.weightInGrams = weightInGrams; }
    public boolean isDataEstimated() { return isDataEstimated; }
    public NutritionalValues getNutritionalValues() { return nutritionValues; }
    public void setNutritionValues(NutritionalValues nutritionValues) { this.nutritionValues = nutritionValues; }

    public double getTotalKcal() {
        return (nutritionValues.getKcal() * weightInGrams) / 100.0;
    }


}
