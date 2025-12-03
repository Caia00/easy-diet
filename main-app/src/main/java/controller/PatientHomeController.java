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
        if (user.getDietPlan() == null) {
            System.out.println("MSG: Non hai ancora una dieta assegnata dal nutrizionista.");
            return;
        }

        System.out.println("DEBUG: Apertura visualizzatore dieta... (To be implemented)");
        // TODO: Implementare DietViewerController
        // DietViewerView dietView = viewFactory.createDietViewerView();
        // new DietViewerController(user, dietView).start();
    }

    /**
     * Caso d'uso: Gestione Lista della Spesa
     */
    public void openShoppingList() {
        System.out.println("DEBUG: Apertura lista della spesa...");

        // 1. Chiedo alla factory la view corretta (CLI/GUI)
        // ShoppingListView shopView = viewFactory.createShoppingListView();

        // 2. Lancio il controller specifico
        // new ShoppingListController(user, daoFactory, viewFactory, shopView).start();
    }

    /**
     * Caso d'uso: Logout
     */
    public void logout() {
        System.out.println("Disconnessione in corso...");
        view.close();
        // Nota: Il programma tornerà al LoginController o terminerà,
        // a seconda di come hai gestito il flusso nel Main.
    }
}
