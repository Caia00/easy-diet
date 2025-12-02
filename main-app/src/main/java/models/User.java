package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User extends Profile{
    private double heightCm;
    private double currentWeightKg;
    private String gender;

    private DietPlan dietPlan;

    private List<ShoppingList> shoppingLists = new ArrayList<>();

    public User(String name, String surname, String email, String password, LocalDate birthDate,
                double heightCm, double currentWeightKg, String gender) {
        super(name, surname, email, password, birthDate);
        this.heightCm = heightCm;
        this.currentWeightKg = currentWeightKg;
        this.gender = gender;
        this.dietPlan = null;
    }


    public void assignDiet(DietPlan plan) {
        this.dietPlan = plan;
    }

    public DietPlan getDietPlan() {
        return dietPlan;
    }

    public void saveShoppingList(ShoppingList list) {
        if (list != null) {
            this.shoppingLists.add(list);
        }
    }

    public List<ShoppingList> getShoppingHistory() {
        return shoppingLists;
    }

    public ShoppingList getListByName(String name) {
        for (ShoppingList list : shoppingLists) {
            if (list.getListName().equalsIgnoreCase(name)) {
                return list;
            }
        }
        return null;
    }

    public void deleteList(ShoppingList list) {
        shoppingLists.remove(list);
    }

    public double getHeightCm(){
        return heightCm;
    }

    public double getCurrentWeightKg(){
        return currentWeightKg;
    }

    public String getGender(){
        return gender;
    }

    @Override
    public String getRole() {
        return "PATIENT";
    }
}
