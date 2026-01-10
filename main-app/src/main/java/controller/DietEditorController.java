package controller;

import models.*;
import models.beans.FoodBean;
import models.factory.DAOFactory;
import view.DietEditorView;
import view.DietViewerView;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.*;

public class DietEditorController {
    private static final Logger logger = Logger.getLogger(DietEditorController.class.getName());
    private final Nutritionist nutritionist;
    private final DietPlan plan;
    private final DAOFactory daoFactory;


    private final DietEditorView editorView;
    private final DietViewerView viewerView;

    public DietEditorController(Nutritionist nutritionist, DietPlan plan, DAOFactory daoFactory,
                                DietEditorView editorView, DietViewerView viewerView) {
        this.nutritionist = nutritionist;
        this.plan = plan;
        this.daoFactory = daoFactory;
        this.editorView = editorView;
        this.viewerView = viewerView;

        this.editorView.setController(this);
    }

    public void start() {
        showCurrentPlanState();
        editorView.start();
    }


    private void showCurrentPlanState() {
        viewerView.showDietPlan(plan);
    }


    public void addMeal(String day, String mealName, String timeStr) {
        try {
            LocalTime time = (timeStr == null || timeStr.isEmpty()) ? null : LocalTime.parse(timeStr);

            Meal newMeal = new Meal(mealName, time);
            plan.addMealToDay(day, newMeal);

            editorView.showMessage("Pasto aggiunto con successo.");
            showCurrentPlanState();

        } catch (DateTimeParseException e3) {
            editorView.showError("Formato orario non valido. Usa HH:mm (es. 12:30).");
        } catch (IllegalArgumentException ex){
            editorView.showError(ex.getMessage());
        } catch (Exception e) {
            editorView.showError("Errore generico: " + e.getMessage());
        }
    }

    public void addFoodItem(FoodBean bean) {


        Meal meal = plan.getMeal(bean.getDay(), bean.getMealName());

        if (meal == null) {
            editorView.showError("Pasto '" + bean.getMealName() + "' non trovato in " + bean.getDay() + ".");
            return;
        }

        NutritionalTarget target = new NutritionalTarget(bean.getAppCategory(), bean.getKcal(), bean.getProt(), bean.getCarb(), bean.getSug(), bean.getFat(), bean.getFib());

        //Creazione del suggestProduct, se il nome dovesse essere inserito sarà creato il CommercialProduct con dati fittizi
        CommercialProduct product = null;
        if (bean.getSuggestedProductName() != null && !bean.getSuggestedProductName().trim().isEmpty()) {
            product = new CommercialProduct(bean.getSuggestedProductName(), 0, 0, bean.getAppCategory(), new NutritionalValues(), false);
        }

        try {
            meal.addFoodItem(new DietItem(target, product));
        } catch (IllegalArgumentException e){
            editorView.showError(e.getMessage());
            return;
        }

        editorView.showMessage("Alimento aggiunto!");
        showCurrentPlanState();
    }

    public void removeMeal(String day, String mealName) {
        boolean removed = plan.removeMeal(day, mealName);
        if (removed) {
            editorView.showMessage("Pasto rimosso.");
            showCurrentPlanState();
        } else {
            editorView.showError("Pasto non trovato.");
        }
    }

    public void removeFoodItem(String day, String mealName, int itemIndex) {

        Meal meal = plan.getMeal(day, mealName);

        if (meal != null) {
            if (itemIndex >= 0 && itemIndex < meal.getFoods().size()) {

                DietItem removedItem = meal.getFoods().get(itemIndex);
                meal.removeFoodItem(itemIndex);

                editorView.showMessage("Rimosso: " + removedItem.getCategory());
                showCurrentPlanState();

            } else {
                editorView.showError("Numero alimento non valido.");
            }
        } else {
            editorView.showError("Pasto non trovato in " + day);
        }
    }

    public void renameDiet(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            editorView.showError("Il nome della dieta non può essere vuoto.");
            return;
        }

        plan.setDietName(newName);

        editorView.showMessage("Dieta rinominata correttamente in: " + newName);

        showCurrentPlanState();
    }


    public void saveAndExit() {
        logger.info(nutritionist.getEmail() + " salvataggio modifiche dieta...");

        daoFactory.getDietPlanDAO().save(plan, nutritionist.getEmail());

        if (!nutritionist.getDietTemplates().contains(plan)) {
            nutritionist.getDietTemplates().add(plan);
        }

        editorView.showMessage("Dieta salvata correttamente.");
        editorView.close();
    }

    public void cancel() {
        editorView.close();
    }
}
