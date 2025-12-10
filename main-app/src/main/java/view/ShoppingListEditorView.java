package view;

import controller.ShoppingListEditorController;
import models.AppCategory;
import models.CommercialProduct;
import models.ShoppingList;

import java.util.List;
import java.util.Map;

public interface ShoppingListEditorView {
    void setController(ShoppingListEditorController controller);
    void start();


    void showCurrentList(ShoppingList list);

    void showDietStatus(Map<AppCategory, String> demandsStatus);

    void showCatalogProducts(AppCategory category, List<CommercialProduct> products);

    int askQuantityWithAdvice(CommercialProduct product, String suggestionMsg, int suggestedQty);

    boolean showUnmetDemandsWarning(List<String> missingCategories);

    void showMessage(String msg);
    void showError(String err);
    void close();
}
