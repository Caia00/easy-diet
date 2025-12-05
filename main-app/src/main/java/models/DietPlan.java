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
        String key = normalizeDay(day);
        if (weeklySchedule.containsKey(key)) {
            weeklySchedule.get(day).add(meal);
        } else {
            System.err.println("Giorno non valido: " + day + " (Interpretato come: " + key + ")");
            throw new IllegalArgumentException("Giorno non valido: " + day);
        }
    }


    public Meal getMeal(String day, String mealName) {
        String key = normalizeDay(day);
        List<Meal> mealsOfDay = getMealsForDay(key);
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
        String key = normalizeDay(day);
        if (!weeklySchedule.containsKey(key)) return false;

        List<Meal> mealsOfDay = weeklySchedule.get(key);
        return mealsOfDay.removeIf(m -> m.getName().equalsIgnoreCase(mealName));
    }


    public List<Meal> getMealsForDay(String day) {
        String key = normalizeDay(day);
        return weeklySchedule.getOrDefault(key, new ArrayList<>());
    }

    //Metodo usato per normalizzare la stringa in input per non ricevere errori sgraditi
    private String normalizeDay(String inputDay) {
        if (inputDay == null) return "";

        String clean = inputDay.trim().toLowerCase();

        if (clean.startsWith("lun")) return "Lunedì";
        if (clean.startsWith("mar")) return "Martedì";
        if (clean.startsWith("mer")) return "Mercoledì";
        if (clean.startsWith("gio")) return "Giovedì";
        if (clean.startsWith("ven")) return "Venerdì";
        if (clean.startsWith("sab")) return "Sabato";
        if (clean.startsWith("dom")) return "Domenica";

        return inputDay;
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
