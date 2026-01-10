package controller;

import models.Nutritionist;
import models.factory.DAOFactory;
import models.factory.ViewFactory;
import view.DietManagerView;
import view.NutritionistHomeView;
import java.util.logging.*;


public class NutritionistHomeController {
    private static final Logger logger = Logger.getLogger(NutritionistHomeController.class.getName());
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
        logger.info(nutritionist.getEmail() +" apertura Diet Manager...");

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
        view.showError("Futura implementazione...");
        /** TODO: implementare controller e view per gestione profilo
         *
         */
    }

    public void logout() {
        logger.info(nutritionist.getEmail() +" disconnessione in corso...");
        view.close();
        new LoginController(daoFactory, viewFactory).start();
    }
}
