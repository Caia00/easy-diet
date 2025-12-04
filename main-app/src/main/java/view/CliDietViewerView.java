package view;

import controller.DietViewerController;
import models.DietItem;
import models.DietPlan;
import models.Meal;

import java.util.List;
import java.util.Scanner;

public class CliDietViewerView implements DietViewerView {
    private DietViewerController controller;
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void setController(DietViewerController controller) {
        this.controller = controller;
    }

    @Override
    public void showDietPlan(DietPlan plan) {
        System.out.println("\n=== VISUALIZZAZIONE DIETA: " + plan.getDietName().toUpperCase() + " ===");

        String[] days = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

        for (String day : days) {
            List<Meal> meals = plan.getMealsForDay(day);
            System.out.println("\n--- " + day.toUpperCase() + " ---");

            if (meals.isEmpty()) {
                System.out.println(" (Nessun pasto previsto)");
            } else {
                for (Meal meal : meals) {
                    System.out.println(" > " + meal.getName() + " (" + meal.getTime() + ")");
                    for (DietItem item : meal.getFoods()) {
                        System.out.println("    - " + item.toString());
                    }
                    System.out.printf("    [Tot Kcal: %.0f]\n", meal.getTotalKcalTarget());
                }
            }
        }
        System.out.println("\n===========================================");
    }

    @Override
    public void start() {
        System.out.println("Premi INVIO per tornare alla Home...");
        scanner.nextLine();
        controller.exit();
    }

    @Override
    public void close() {

    }
}
