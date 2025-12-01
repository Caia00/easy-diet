package models;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String name;
    private LocalTime time; //Utile in futuro per possibile sistema notifiche

    private List<DietItem> foods;

    public Meal(String name, LocalTime time) {
        this.name = name;
        this.time = time;
        this.foods = new ArrayList<>();
    }

    //Metodi per gestire la lista dei cibi

    public void addFoodItem(DietItem item) {
        if (item != null) {
            this.foods.add(item);
        }
    }

    public void removeFoodItem(DietItem item) {
        this.foods.remove(item);
    }

    //Metodo per rimuovere un cibo dalla lista a seconda dell'indice al suo interno
    public void removeFoodItem(int index) {
        if (index >= 0 && index < foods.size()) {
            foods.remove(index);
        }
    }

    public List<DietItem> getFoods() {
        return foods;
    }

    //Metodi per calcolare le quantità di nutrienti target totali del pasto

    public double getTotalKcalTarget() {
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetKcal()).sum();
    }

    public double getTotalCarbsTarget(){
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetCarbs()).sum();
    }

    public double getTotalSugarTarget(){
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetSugar()).sum();
    }

    public double getTotalFatsTarget(){
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetFats()).sum();
    }

    public double getTotalFibersTarget(){
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetFibers()).sum();
    }

    public double getTotalProteinsTarget() {
        return foods.stream().mapToDouble(i -> i.getTarget().getTargetProteins()).sum();
    }

    // --- GETTERS & SETTERS ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

}
