package controller;

import models.*;
import models.beans.MealDemand;
import models.beans.ShoppingCalculationResult;
import models.factory.DAOFactory;
import models.services.DietCalculatorService;
import view.ShoppingListEditorView;

import java.util.*;
import java.util.stream.Collectors;

public class ShoppingListEditorController {
    private final User user;
    private final ShoppingList shoppingList;
    private final DAOFactory daoFactory;
    private final ShoppingListEditorView view;
    private final List<CommercialProduct> fullCatalog;

    private List<MealDemand> totalDemands;
    private List<MealDemand> remainingDemands;

    public ShoppingListEditorController(User user, ShoppingList shoppingList, DAOFactory daoFactory,
                                        ShoppingListEditorView view, List<CommercialProduct> fullCatalog) {
        this.user = user;
        this.shoppingList = shoppingList;
        this.daoFactory = daoFactory;
        this.view = view;
        this.fullCatalog = fullCatalog;

        this.view.setController(this);
        initDemands();
    }

    private void initDemands() {
        this.totalDemands = extractDemandsFromDiet(user.getDietPlan());
        this.remainingDemands = new ArrayList<>(totalDemands);
        /* Non viene gestito il caso di una lista che sta venendo modificata
           TODO (futuro) : Ricalcolo dei remainingDemands
         */
    }

    public void start() {
        refreshDashboard();
        view.start();
    }

    private void refreshDashboard() {
        view.showCurrentList(shoppingList);

        //Calcolo statistiche X/Y, X = mealDemand coperte, Y = mealDemand da coprire
        Map<AppCategory, String> statusMap = new EnumMap<>(AppCategory.class);

        Map<AppCategory, Long> totalCounts = totalDemands.stream()
                .collect(Collectors.groupingBy(d -> d.getTarget().getCategory(), Collectors.counting()));

        Map<AppCategory, Long> remainingCounts = remainingDemands.stream()
                .collect(Collectors.groupingBy(d -> d.getTarget().getCategory(), Collectors.counting()));


        for (AppCategory cat : AppCategory.values()) {
            if (totalCounts.containsKey(cat)) {
                long total = totalCounts.get(cat);
                long left = remainingCounts.getOrDefault(cat, 0L);
                long covered = total - left;
                statusMap.put(cat, covered + "/" + total);
            }
        }

        view.showDietStatus(statusMap);
    }


    //Filtraggio dei prodotti del catalogo tramite la categoria
    public void selectCategory(AppCategory category) {
        List<CommercialProduct> filteredProducts = fullCatalog.stream()
                .filter(p -> p.getCategory() == category)
                .toList();

        if (filteredProducts.isEmpty()) {
            view.showError("Nessun prodotto trovato nel catalogo per: " + category);
            return;
        }

        view.showCatalogProducts(category, filteredProducts);
    }

    //Selezione di un prodotto dal catalogo filtrato, se la categoria serve per la dieta il sistema calcola i suggerimenti per l'utente
    public void selectProduct(CommercialProduct product) {
        //Prendo tutte le richieste pertinenti col prodotto (le category sono uguali)
        List<MealDemand> relevantDemands = remainingDemands.stream()
                .filter(d -> d.getTarget().getCategory() == product.getCategory())
                .toList();

        String adviceMsg;
        int suggestedQty = 1; //Default

        //Se ci sono richieste pertinenti uso il DietCalculatorService per il calcolo delle confezioni
        if (!relevantDemands.isEmpty()) {
            ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(relevantDemands, product);

            if (result.getPacksToBuy() > 0) {
                adviceMsg = String.format("DIETA: Servono %.0fg totali per coprire %d pasti.%nConsiglio: %d confezioni.",
                        result.getTotalGramsRequired(), relevantDemands.size(), result.getPacksToBuy());
                suggestedQty = result.getPacksToBuy();
            } else {
                adviceMsg = "DIETA: Questo prodotto non sembra adatto o i valori nutrizionali sono incompleti.";
            }
        } else {
            adviceMsg = "EXTRA: Questa categoria è già coperta o non richiesta dalla dieta.";
        }

        int chosenQty = view.askQuantityWithAdvice(product, adviceMsg, suggestedQty);

        if (chosenQty > 0) {
            addProductWithLogic(product, chosenQty, suggestedQty, relevantDemands);
        }
    }

    //Aggiunta del prodotto selezionato alla lista con quantità selezionata
    private void addProductWithLogic(CommercialProduct product, int chosenQty, int suggestedQty, List<MealDemand> demandsToCover) {

        boolean isDiet = totalDemands.stream().anyMatch(d -> d.getTarget().getCategory() == product.getCategory());
        shoppingList.addItem(product, chosenQty, isDiet);

        if (!demandsToCover.isEmpty() && chosenQty >= suggestedQty) {
            view.showMessage("Ottimo! Quantità sufficiente per coprire i pasti scoperti.");
        } else if (isDiet) {
            view.showMessage("Prodotto dietetico aggiunto (copertura supplementare o alternativa).");
        } else {
            view.showMessage("Prodotto Extra aggiunto.");
        }

        recalculateRemainingDemands();
        refreshDashboard();
    }

    public void removeProduct(int index, int quantityToRemove) {
        if (index < 0 || index >= shoppingList.getItems().size()) {
            view.showError("Indice prodotto non valido.");
            return;
        }

        ShoppingItem item = shoppingList.getItems().get(index);
        int currentQty = item.getQuantity();

        if (quantityToRemove >= currentQty) {
            shoppingList.removeProductCompletely(item);
            view.showMessage("Rimosso completamente: " + item.getProduct().getName());
        } else {
            shoppingList.decreaseQuantity(item, quantityToRemove);
            view.showMessage("Rimossi " + quantityToRemove + " pezzi di " + item.getProduct().getName());
        }

        recalculateRemainingDemands();
        refreshDashboard();
    }


    public void saveAndExit() {
        //Controllo finale, se ci sono categoria nella dieta la cui demand non è stata soddisfatta viene comunicato così che l'utente può decidere se salvare la dieta così com'è o aggiungere le cose rimanenti
        if (!remainingDemands.isEmpty()) {
            List<String> missing = remainingDemands.stream()
                    .map(d -> d.getTarget().getCategory().name())
                    .distinct()
                    .toList();

            boolean proceed = view.showUnmetDemandsWarning(missing);
            if (!proceed) return;
        }

        daoFactory.getShoppingListDAO().save(shoppingList, user.getEmail());
        view.showMessage("Lista salvata correttamente!");
        view.close();
    }

    private List<MealDemand> extractDemandsFromDiet(DietPlan plan) {
        List<MealDemand> list = new ArrayList<>();
        if (plan == null) return list;
        for (var entry : plan.getWeeklySchedule().entrySet()) {
            String day = entry.getKey();
            for (Meal meal : entry.getValue()) {
                for (DietItem item : meal.getFoods()) {
                    list.add(new MealDemand(day, meal.getName(), item.getTarget()));
                }
            }
        }
        return list;
    }

    //Metodo privato per ricalcolare le MealDemands rimanenti (da soddisfare) in base ai prodotti presenti nella lista.
    private void recalculateRemainingDemands() {
        this.remainingDemands = new ArrayList<>(totalDemands);

        for (ShoppingItem item : shoppingList.getItems()) {

            CommercialProduct product = item.getProduct();

            double availableGrams = product.getWeightInGrams() * item.getQuantity();

            //Trovo demands non soddisfatte che matchano con categoria prodotto
            List<MealDemand> candidates = remainingDemands.stream()
                    .filter(d -> d.getTarget().getCategory() == product.getCategory())
                    .toList();

            if (!item.isForDiet() || candidates.isEmpty()) continue;

            //Uso il DietCalculatorService per sapere per ogni domanda quanti grammi del determinato prodotto serviranno
            ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(candidates, product);
            Map<String, Double> costMap = result.getGramsPerMealDetail();
            eliminateDemandIfSatisfied(candidates, costMap, availableGrams);
        }
    }


    private void eliminateDemandIfSatisfied(List<MealDemand> candidates, Map<String, Double> costMap, double availableGrams) {

        //Elimino le domande finché ho grammi di prodotto disponibili, utilizzo iteratore per poter eliminare elementi mentre scorro
        var iterator = candidates.iterator();
        while (iterator.hasNext() && availableGrams > 0) {
            MealDemand demand = iterator.next();

            String key = demand.getDayOfWeek() + " - " + demand.getMealName();

            Double gramsNeeded = costMap.get(key);

            if (gramsNeeded != null && gramsNeeded > 0) {
                if (availableGrams >= gramsNeeded) {

                    availableGrams -= gramsNeeded;

                    remainingDemands.remove(demand);

                } else {
                    break;
                }
            }
        }
    }

    public ShoppingList getCurrentList() {
        return shoppingList;
    }

    public void cancel(){
        view.close();
    }
}
