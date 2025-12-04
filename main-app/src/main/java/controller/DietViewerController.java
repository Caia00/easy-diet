package controller;

import models.DietPlan;
import view.DietViewerView;


public class DietViewerController {
    private final DietPlan plan;
    private final DietViewerView view;

    public DietViewerController(DietPlan plan, DietViewerView view) {
        this.plan = plan;
        this.view = view;
        this.view.setController(this);
    }

    public void start() {
        if (plan == null) {
            System.out.println("ERROR: Nessun piano da visualizzare.");
            return;
        }
        view.showDietPlan(plan);
        view.start();
    }

    public void exit() {
        view.close();
    }
}
