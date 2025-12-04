package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietPlan {
    private String dietName;
    private Integer id;
    private Map<String, List<Meal>> weeklySchedule;

    public DietPlan(String dietName) {
        this.dietName = dietName;
        this.weeklySchedule = new HashMap<>();
        this.id = null;
        initializeWeek();
    }

    private void initializeWeek() {
        String[] days = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        for (String day : days) {
            weeklySchedule.put(day, new ArrayList<>());
        }
    }

    //Metodi per la gestione della Dieta

    public void addMealToDay(String day, Meal meal) {
        if (weeklySchedule.containsKey(day)) {
            weeklySchedule.get(day).add(meal);
        } else {
            System.err.println("WARNING: Giorno non trovato nel piano: " + day);
        }
    }


    public Meal getMeal(String day, String mealName) {
        List<Meal> mealsOfDay = getMealsForDay(day);
        for (Meal meal : mealsOfDay) {
            if (meal.getName().equalsIgnoreCase(mealName)) {
                return meal;
            }
        }
        return null;
    }


    public boolean hasMeal(String day, String mealName) {
        return getMeal(day, mealName) != null;
    }


    public boolean removeMeal(String day, String mealName) {
        if (!weeklySchedule.containsKey(day)) return false;

        List<Meal> mealsOfDay = weeklySchedule.get(day);
        return mealsOfDay.removeIf(m -> m.getName().equalsIgnoreCase(mealName));
    }


    public List<Meal> getMealsForDay(String day) {
        return weeklySchedule.getOrDefault(day, new ArrayList<>());
    }


    public double getDailyKcalTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalKcalTarget).sum();
    }

    public double getDailyCarbsTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalCarbsTarget).sum();
    }

    public double getDailySugarTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalSugarTarget).sum();
    }

    public double getDailyFatsTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalFatsTarget).sum();
    }

    public double getDailyFibersTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalFibersTarget).sum();
    }

    public double getDailyProteinsTarget(String day) {
        return getMealsForDay(day).stream().mapToDouble(Meal::getTotalProteinsTarget).sum();
    }

    public String getDietName() { return dietName; }
    public void setDietName(String dietName) { this.dietName = dietName; }

    public Integer getDietId() { return id; }
    public void setDietId(Integer id) { this.id = id; }

    public Map<String, List<Meal>> getWeeklySchedule() { return weeklySchedule; }

    @Override
    public String toString() {
        return String.format("Dieta: %s (ID: %s)", dietName, (id != null ? id : "Non salvata"));
    }
}
