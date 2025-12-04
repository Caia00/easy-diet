package controller;

import models.*;
import models.DAO.*;
import models.factory.*;
import view.*;

public class PatientHomeController {
    private final User user;
    private final DAOFactory daoFactory;
    private final ViewFactory viewFactory;
    private final PatientHomeView view;

    public PatientHomeController(User user, DAOFactory daoFactory, ViewFactory viewFactory, PatientHomeView view) {
        this.user = user;
        this.daoFactory = daoFactory;
        this.viewFactory = viewFactory;
        this.view = view;
        this.view.setController(this);

    }

    public void start() {
        view.showWelcomeMessage(user.getName() + " " + user.getSurname());
        view.start();
    }


    public void openMyDiet() {

        if (user.getDietPlan() != null) {
            System.out.println("LOG: Dieta trovata in memoria (Cache).");
            launchDietViewer(user.getDietPlan());
            return;
        }

        System.out.println("LOG: Dieta non in memoria. Ricerca tramite DAO...");

        DietPlan loadedPlan = daoFactory.getDietPlanDAO().findByOwner(user.getEmail());

        if (loadedPlan != null) {
            System.out.println("DEBUG: Dieta trovata nel DB. Aggiorno la sessione utente.");

            user.assignDiet(loadedPlan);

            launchDietViewer(loadedPlan);

        } else {
            view.showErrorMessage("Non hai ancora una dieta assegnata dal nutrizionista.");
        }
    }

    public void openShoppingList() {
        System.out.println("DEBUG: Apertura lista della spesa...");

        // 1. Chiedo alla factory la view corretta (CLI/GUI)
        // ShoppingListView shopView = viewFactory.createShoppingListView();

        // 2. Lancio il controller specifico
        // new ShoppingListController(user, daoFactory, viewFactory, shopView).start();
    }


    public void logout() {
        System.out.println("Disconnessione in corso...");
        view.close();
    }

    private void launchDietViewer(DietPlan plan) {
        DietViewerView viewerView = viewFactory.createDietViewerView();
        DietViewerController viewerController = new DietViewerController(plan, viewerView);
        viewerController.start();
    }
}
