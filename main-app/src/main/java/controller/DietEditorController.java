package controller;

import models.*;
import models.factory.DAOFactory;
import view.DietEditorView;
import view.DietViewerView;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class DietEditorController {
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

        } catch (DateTimeParseException e) {
            editorView.showError("Formato orario non valido. Usa HH:mm (es. 12:30).");
        } catch (IllegalArgumentException e){
            editorView.showError(e.getMessage());
        } catch (Exception e) {
            editorView.showError("Errore generico: " + e.getMessage());
        }
    }

    public void addFoodItem(String day, String mealName,
                            AppCategory category, double kcal, double prot, double carb, double sug, double fat, double fib,
                            String suggestedProductName) {


        Meal meal = plan.getMeal(day, mealName);

        if (meal == null) {
            editorView.showError("Pasto '" + mealName + "' non trovato in " + day + ".");
            return;
        }

        NutritionalTarget target = new NutritionalTarget(category, kcal, prot, carb, sug, fat, fib);

        //Creazione del suggestProduct, se il nome dovesse essere inserito sarà creato il CommercialProduct con dati fittizi
        CommercialProduct product = null;
        if (suggestedProductName != null && !suggestedProductName.trim().isEmpty()) {
            product = new CommercialProduct(suggestedProductName, 0, 0, category, new NutritionalValues(), false);
        }

        meal.addFoodItem(new DietItem(target, product));

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
        System.out.println("LOG: Salvataggio modifiche...");

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
