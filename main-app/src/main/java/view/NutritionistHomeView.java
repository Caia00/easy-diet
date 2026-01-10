package view;

import controller.*;

public interface NutritionistHomeView {
    void setController(NutritionistHomeController controller);
    void start(); // Mostra il menu principale
    void showWelcome(String name);
    void showError(String err);
    void close();
}
