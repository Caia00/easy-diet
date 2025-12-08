package controller;

import models.DietPlan;
import models.Nutritionist;
import models.Profile;
import models.factory.DAOFactory;
import models.factory.ViewFactory;
import view.DietEditorView;
import view.DietManagerView;
import view.DietViewerView;

import java.util.List;

public class DietManagerController {
    private final Nutritionist nutritionist;
    private final DAOFactory daoFactory;
    private final ViewFactory viewFactory;
    private final DietManagerView view;

    public DietManagerController(Nutritionist nutritionist, DAOFactory daoFactory, ViewFactory viewFactory, DietManagerView view) {
        this.nutritionist = nutritionist;
        this.daoFactory = daoFactory;
        this.viewFactory = viewFactory;
        this.view = view;
        this.view.setController(this);
    }

    public void start() {
        refreshList();
        view.start();
    }

    private void refreshList() {
        List<DietPlan> summaries = daoFactory.getDietPlanDAO().findAllSummariesByCreator(nutritionist.getEmail());

        nutritionist.getDietTemplates().clear();
        nutritionist.getDietTemplates().addAll(summaries);

        view.showDietList(summaries);
    }

    public void createDiet(String name) {
        if (name.isEmpty()) return;
        DietPlan newPlan = new DietPlan(name);

        launchEditor(newPlan);
    }

    public void editDiet(DietPlan summary) {
        daoFactory.getDietPlanDAO().loadPlanDetails(summary);
        launchEditor(summary);
    }

    public void deleteDiet(DietPlan summary) {
        daoFactory.getDietPlanDAO().delete(summary);
        view.showMessage("Dieta eliminata.");
        refreshList();
    }

    public void assignDiet(DietPlan summary, String patientEmail) {
        if (patientEmail == null || patientEmail.trim().isEmpty()) {
            view.showError("Inserisci un'email valida.");
            return;
        }

        Profile profile = daoFactory.getProfileDAO().findByEmail(patientEmail);

        if (profile == null) {
            view.showError("Nessun utente trovato con questa email.");
            return;
        }

        if (profile instanceof Nutritionist) {
            view.showError("Non puoi assegnare una dieta a un altro Nutrizionista!");
            return;
        }

        System.out.println("LOG: Assegnazione dieta '" + summary.getDietName() + "' a " + patientEmail + "...");

        boolean success = daoFactory.getProfileDAO().assignDiet(patientEmail, summary);

        if (success) {
            view.showMessage("Dieta assegnata con successo al paziente: " + profile.getName() + " " + profile.getSurname());
        } else {
            view.showError("Errore durante l'assegnazione (Database error).");
        }
    }

    public void back() {
        view.close();
    }

    private void launchEditor(DietPlan plan) {
        System.out.println("LOG: Apertura Editor per la dieta '" + plan.getDietName() + "'...");

        DietEditorView editorView = viewFactory.createDietEditorView();
        DietViewerView viewerView = viewFactory.createDietViewerView();

        DietEditorController editorController = new DietEditorController(
                nutritionist,
                plan,
                daoFactory,
                editorView,
                viewerView
        );
        editorController.start();

        refreshList();

        System.out.println("LOG: Ritorno al Diet Manager.");
    }


}
