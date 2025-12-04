package view;

import controller.DietViewerController;
import models.DietPlan;

public interface DietViewerView {
    void setController(DietViewerController controller);
    void start();

    void showDietPlan(DietPlan plan);

    void close();
}
