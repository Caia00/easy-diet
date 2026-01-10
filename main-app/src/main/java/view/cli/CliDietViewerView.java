package view.cli;

import models.DietItem;
import models.DietPlan;
import models.Meal;
import view.DietViewerView;

import java.util.List;

public class CliDietViewerView implements DietViewerView {


    @Override
    public void showDietPlan(DietPlan plan) {
        System.out.println("\n=== VISUALIZZAZIONE DIETA: " + plan.getDietName().toUpperCase() + " ===");

        String[] days = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        for (String day : days) {
            showDayPlan(day, plan.getMealsForDay(day));
        }
        System.out.println("\n===========================================");
    }

    private void showDayPlan(String day, List<Meal> meals) {
        System.out.println("\n--- " + day.toUpperCase() + " ---");

        if (meals.isEmpty()) {
            System.out.println(" (Nessun pasto previsto)");
            return;
        }
        for (Meal meal : meals) {
            showMealDetails(meal);
        }
    }

    private void showMealDetails(Meal meal) {
        System.out.println(" > " + meal.getName() + " (" + meal.getTime() + ")");

        List<DietItem> foods = meal.getFoods();
        if (foods.isEmpty()) {
            System.out.println("    (Nessun alimento)");
        } else {
            for (int i = 0; i < foods.size(); i++) {
                System.out.println("    " + (i + 1) + ". " + foods.get(i).toString());
            }
        }
        System.out.printf("    [Tot Kcal: %.0f]%n", meal.getTotalKcalTarget());
    }

}
