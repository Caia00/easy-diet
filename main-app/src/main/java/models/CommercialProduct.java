package models;

public class CommercialProduct implements Edible{
    private String name;
    private double price;
    private appCategory category;
    private nutritionalValues nutritionValues;
    private boolean isDataEstimated; // Flag utilizzata per specificare se valori presi da catalogo o da fallback db

    public CommercialProduct(String name, double price, appCategory category, nutritionalValues nutritionValues, boolean isDataEstimated) {
        this.name = name;
        this.price = price;
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
    public boolean isDataEstimated() { return isDataEstimated; }

}
