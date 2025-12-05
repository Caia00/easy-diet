package controller;

import models.DietPlan;
import models.Nutritionist;
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
        // Object Hydration: carico i dettagli prima di editare
        daoFactory.getDietPlanDAO().loadPlanDetails(summary);
        launchEditor(summary);
    }

    public void deleteDiet(DietPlan summary) {
        daoFactory.getDietPlanDAO().delete(summary);
        view.showMessage("Dieta eliminata.");
        refreshList();
    }

    public void assignDiet(DietPlan summary, String patientEmail) {
        // TODO: Implementare logica di assegnazione
        // daoFactory.getProfileDAO().assignDietToUser(patientEmail, summary.getId());
        view.showMessage("Dieta assegnata correttamente!");
    }

    public void back() {
        view.close();
    }

    private void launchEditor(DietPlan plan) {
        System.out.println("LOG: Apertura Editor per la dieta '" + plan.getDietName() + "'...");

        DietEditorView editorView = viewFactory.createDietEditorView();
        DietViewerView viewerView = viewFactory.createDietViewerView();

        DietEditorController editorCtrl = new DietEditorController(
                nutritionist,
                plan,
                daoFactory,
                editorView,
                viewerView
        );
        editorCtrl.start();

        refreshList();

        System.out.println("LOG: Ritorno al Diet Manager.");
    }


}
