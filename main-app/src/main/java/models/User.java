package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.beans.ProfileBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User extends Profile{
    private double heightCm;
    private double currentWeightKg;
    private String gender;

    private DietPlan dietPlan;

    private List<ShoppingList> shoppingLists = new ArrayList<>();

    public User(){
        super();
    }

    public User(ProfileBean bean, double heightCm, double currentWeightKg, String gender) {
        super(bean.getName(), bean.getSurname(), bean.getEmail(), bean.getPassword(), bean.getBirthdate());
        this.heightCm = heightCm;
        this.currentWeightKg = currentWeightKg;
        this.gender = gender;
        this.dietPlan = null;
    }

    @JsonIgnore
    public void assignDiet(DietPlan plan) {
        this.dietPlan = plan;
    }

    public DietPlan getDietPlan() {
        return dietPlan;
    }
    public void setDietPlan(DietPlan dietPlan) {
        this.dietPlan = dietPlan;
    }

    public void saveShoppingList(ShoppingList list) {
        if (list != null) {
            this.shoppingLists.add(list);
        }
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }
    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public ShoppingList getListByName(String name) {
        for (ShoppingList list : shoppingLists) {
            if (list.getListName().equalsIgnoreCase(name)) {
                return list;
            }
        }
        return null;
    }

    public void deleteList(String listName) {
        ShoppingList list = getListByName(listName);
        shoppingLists.remove(list);
    }

    public double getHeightCm(){
        return heightCm;
    }
    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getCurrentWeightKg(){
        return currentWeightKg;
    }
    public void setCurrentWeightKg(double currentWeightKg) {
        this.currentWeightKg = currentWeightKg;
    }

    public String getGender(){
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getRole() {
        return "PATIENT";
    }

    @Override
    public String toString() {
        return String.format("Paziente: %s %s (%s) - Peso: %.1fkg",
                name, surname, email, currentWeightKg);
    }
}
