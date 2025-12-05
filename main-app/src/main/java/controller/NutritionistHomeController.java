package controller;

import models.DietPlan;
import models.Nutritionist;
import models.factory.DAOFactory;
import models.factory.ViewFactory;
import view.DietManagerView;
import view.NutritionistHomeView;

import java.util.List;

public class NutritionistHomeController {
    private final Nutritionist nutritionist;
    private final DAOFactory daoFactory;
    private final ViewFactory viewFactory;
    private final NutritionistHomeView view;

    public NutritionistHomeController(Nutritionist nutritionist, DAOFactory daoFactory, ViewFactory viewFactory, NutritionistHomeView view) {
        this.nutritionist = nutritionist;
        this.daoFactory = daoFactory;
        this.viewFactory = viewFactory;
        this.view = view;
        this.view.setController(this);
    }

    public void start() {
        view.showWelcome(nutritionist.getName());
        view.start();
    }


    public void goToDietManager() {
        System.out.println("LOG: Apertura Diet Manager...");

        DietManagerView managerView = viewFactory.createDietManagerView();

        DietManagerController managerController = new DietManagerController(
                nutritionist,
                daoFactory,
                viewFactory,
                managerView
        );

        managerController.start();

        view.showWelcome(nutritionist.getName());
    }

    public void goToProfile() {
        /** TODO: implementare controller e view per gestione profilo
         *
         */
    }

    public void logout() {
        view.close();
    }
}
