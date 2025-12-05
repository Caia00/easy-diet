package view;

import models.DietPlan;
import controller.DietManagerController;

import java.util.List;

public interface DietManagerView {

    void setController(DietManagerController controller);

    void showDietList(List<DietPlan> summaries);

    void start();

    void showMessage(String message);

    void showError(String message);

    void close();


}
