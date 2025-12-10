package models;

public interface Edible {
    double getKcalPer100g();
    double getCarbsPer100g();
    double getSugarPer100g();
    double getFatsPer100g();
    double getFibersPer100g();
    double getProteinsPer100g();
    AppCategory getCategory();
}
