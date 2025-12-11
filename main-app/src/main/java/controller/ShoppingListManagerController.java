package controller;

import models.*;
import models.factory.CatalogLoaderFactory;
import models.factory.DAOFactory;
import models.factory.ViewFactory;
import view.ShoppingListEditorView;
import view.ShoppingListManagerView;

import java.util.List;

public class ShoppingListManagerController {
    private final User user;
    private final DAOFactory daoFactory;
    private final ViewFactory viewFactory;
    private final ShoppingListManagerView view;

    public ShoppingListManagerController(User user, DAOFactory daoFactory, ViewFactory viewFactory, ShoppingListManagerView view) {
        this.user = user;
        this.daoFactory = daoFactory;
        this.viewFactory = viewFactory;
        this.view = view;
        this.view.setController(this);
    }

    public void start() {
        refreshHistory();
        view.start();
    }

    private void refreshHistory() {
        List<ShoppingList> history = daoFactory.getShoppingListDAO().findAllSummariesByOwner(user.getEmail());

        user.getShoppingLists().clear();
        user.getShoppingLists().addAll(history);
        view.showShoppingHistory(history);
    }


    public void createNewList(String listName, SupermarketName supermarket) {
        if (listName.isEmpty()) {
            view.showError("Il nome della lista non può essere vuoto.");
            return;
        }

        List<CommercialProduct> catalog = loadCatalogForSupermarket(supermarket);
        if (catalog == null) return;

        ShoppingList newList = new ShoppingList(listName, supermarket);

        daoFactory.getShoppingListDAO().save(newList, user.getEmail());

        user.getShoppingLists().add(newList);

        view.showMessage("Nuova lista creata: " + listName);

        launchEditor(newList, catalog);
    }


    public void openList(ShoppingList summary) {
        if (summary == null) return;

        System.out.println("LOG: Caricamento dettagli lista '" + summary.getListName() + "'...");

        daoFactory.getShoppingListDAO().loadDetails(summary);

        view.showListDetails(summary);
    }



    public void deleteList(ShoppingList summary) {
        if (summary == null) return;
        daoFactory.getShoppingListDAO().delete(summary);

        user.getShoppingLists().remove(summary);
        view.showMessage("Lista eliminata.");
        refreshHistory();
    }

    public void back() {
        view.close();
    }


    private List<CommercialProduct> loadCatalogForSupermarket(SupermarketName market) {

        CatalogLoader loader = CatalogLoaderFactory.getLoader(market);

        if (loader == null) {
            view.showError("Catalogo per " + market + " in via di sviluppo o non disponibile. Scegli CARREFOUR.");
            return null;
        }

        return loader.loadCatalog();
    }

    private void launchEditor(ShoppingList list, List<CommercialProduct> catalog) {
        ShoppingListEditorView editorView = viewFactory.createShoppingListEditorView();

        ShoppingListEditorController editorController = new ShoppingListEditorController(
                user,
                list,
                daoFactory,
                editorView,
                catalog
        );

        editorController.start();
        refreshHistory();
    }
}
