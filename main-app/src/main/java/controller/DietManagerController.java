package controller;

import exception.InvalidPatientException;
import models.DietPlan;
import models.Nutritionist;
import models.Profile;
import models.factory.DAOFactory;
import models.factory.ViewFactory;
import view.DietEditorView;
import view.DietManagerView;
import view.DietViewerView;
import view.NutritionistHomeView;
import java.util.logging.*;
import java.util.List;

public class DietManagerController {
    private static final Logger logger = Logger.getLogger(DietManagerController.class.getName());
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
        DietPlan newPlan;
        try{
            newPlan = nutritionist.createDietTemplate(name);
        }catch (IllegalArgumentException e){
            view.showError(e.getMessage());
            return;
        }

        launchEditor(newPlan);
    }

    public void editDiet(DietPlan summary) {
        daoFactory.getDietPlanDAO().loadPlanDetails(summary);
        launchEditor(summary);
    }

    public void deleteDiet(DietPlan summary) {
        daoFactory.getDietPlanDAO().delete(summary, nutritionist.getEmail());
        view.showMessage("Dieta eliminata.");
        refreshList();
    }

    public void assignDiet(DietPlan summary, String patientEmail) {
        try {
            Profile profile = validatePatient(patientEmail);

            logger.info(() -> "[" + nutritionist.getEmail() + "]" + " assegnazione dieta " + summary.getDietName() + " a " + patientEmail + "...");
            boolean success = daoFactory.getProfileDAO().assignDiet(patientEmail, summary);

            if (success) {
                view.showMessage("Dieta assegnata con successo al paziente: " + profile.getName() + " " + profile.getSurname());
                logger.info(() -> nutritionist.getEmail() + " dieta assegnata con successo al paziente: " + profile.getName() + profile.getSurname());

            } else {
                view.showError("Errore tecnico durante l'assegnazione.");
                logger.severe(() -> nutritionist.getEmail() + " Errore durante l'assegnazione della dieta");
            }

        } catch (InvalidPatientException e) {
            view.showError(e.getMessage());
        } catch (Exception b) {
            view.showError("Errore imprevisto durante l'assegnazione.");
        }
    }

    public void back() {
        view.close();
        NutritionistHomeView homeView = viewFactory.createNutritionistHomeView();

        NutritionistHomeController homeController = new NutritionistHomeController(
                nutritionist,
                daoFactory,
                viewFactory,
                homeView
        );
        logger.info(() -> nutritionist.getEmail() + " Ritorno alla home...");
        homeController.start();
    }

    private void launchEditor(DietPlan plan) {
        logger.info(() -> nutritionist.getEmail() + " Apertura editor per la dieta: " + plan.getDietName());

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

        logger.info(() -> nutritionist.getEmail() + " Ritorno al diet manager...");
    }


    private Profile validatePatient(String email) throws InvalidPatientException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidPatientException("Inserisci un'email valida.");
        }

        Profile profile = daoFactory.getProfileDAO().findByEmail(email);
        if (profile == null) {
            throw new InvalidPatientException("Nessun utente trovato con questa email: " + email);
        }

        if (profile instanceof Nutritionist) {
            throw new InvalidPatientException("Non puoi assegnare una dieta a un altro Nutrizionista!");
        }

        return profile;
    }


}
