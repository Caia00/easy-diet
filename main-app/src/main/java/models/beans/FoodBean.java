package models.beans;

import models.AppCategory;

public class FoodBean {
    private String day;
    private String mealName;
    private AppCategory appCategory;
    private double kcal;
    private double prot;
    private double carb;
    private double sug;
    private double fat;
    private double fib;
    private String suggestedProductName;
    public FoodBean(String day, String mealName, AppCategory cat, double kcal, double prot, double carb, double sug, double fat, double fib, String suggestedProductName) {
        this.day = day;
        this.mealName = mealName;
        this.appCategory = cat;
        this.kcal = kcal;
        this.prot = prot;
        this.carb = carb;
        this.sug = sug;
        this.fat = fat;
        this.fib = fib;
        this.suggestedProductName = suggestedProductName;
    }

    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getMealName() {
        return mealName;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    public AppCategory getAppCategory() {
        return appCategory;
    }
    public void setAppCategory(AppCategory appCategory) {
        this.appCategory = appCategory;
    }
    public double getKcal() {
        return kcal;
    }
    public void setKcal(double kcal) {
        this.kcal = kcal;
    }
    public double getProt() {
        return prot;
    }
    public void setProt(double prot) {
        this.prot = prot;
    }
    public double getCarb() {
        return carb;
    }
    public void setCarb(double carb) {
        this.carb = carb;
    }
    public double getSug() {
        return sug;
    }
    public void setSug(double sug) {
        this.sug = sug;
    }
    public double getFat(){
        return fat;
    }
    public void setFat(double fat){
        this.fat = fat;
    }
    public double getFib(){
        return fib;
    }
    public void setFib(double fib){
        this.fib = fib;
    }
    public String getSuggestedProductName() {
        return suggestedProductName;
    }
    public void setSuggestedProductName(String suggestedProductName) {
        this.suggestedProductName = suggestedProductName;
    }
}
