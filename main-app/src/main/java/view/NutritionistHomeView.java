package view;

import controller.*;
import models.DietPlan;
import java.util.List;

public interface NutritionistHomeView {
    void setController(NutritionistHomeController controller);
    void start(); // Mostra il menu principale
    void showWelcome(String name);
    void close();
}
