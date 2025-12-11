package view;

import models.ShoppingList;
import controller.ShoppingListManagerController;

import java.util.List;

public interface ShoppingListManagerView {
    void setController(ShoppingListManagerController controller);

    void start();

    void showShoppingHistory(List<ShoppingList> history);

    void showListDetails(ShoppingList list);

    void showMessage(String msg);
    void showError(String err);
    void close();
}
