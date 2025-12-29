package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionalValues {
    private double kcal;
    private double carbs;
    private double sugar;
    private double fats;
    private double fibers;
    private double proteins;

    public NutritionalValues() {}
    public NutritionalValues(double kcal, double carbs, double sugar, double fats, double fibers, double proteins) {
        this.kcal = kcal;
        this.carbs = carbs;
        this.sugar = sugar;
        this.fats = fats;
        this.fibers = fibers;
        this.proteins = proteins;
    }

    public NutritionalValues values(){
        return this;
    }

    public double getKcal() {
        return kcal;
    }
    public void setKcal(double kcal) {
        this.kcal = kcal;
    }
    public double getCarbs() {
        return carbs;
    }
    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }
    public double getSugar() {
        return sugar;
    }
    public void setSugar(double sugar) {
        this.sugar = sugar;
    }
    public double getFats() {
        return fats;
    }
    public void setFats(double fats) {
        this.fats = fats;
    }
    public double getFibers() {
        return fibers;
    }
    public void setFibers(double fibers) {
        this.fibers = fibers;
    }
    public double getProteins() {
        return proteins;
    }
    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    @Override
    public String toString() {
        return String.format("[Kcal: %.0f, Prot: %.1f, Carb: %.1f, Zuc: %.1f, Gra: %.1f, Fib: %.1f]",
                kcal, proteins, carbs, sugar, fats, fibers);
    }

}
