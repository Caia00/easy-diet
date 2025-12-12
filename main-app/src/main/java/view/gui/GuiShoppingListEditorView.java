package view.gui;

import controller.ShoppingListEditorController;
import models.AppCategory;
import models.CommercialProduct;
import models.ShoppingList;
import view.ShoppingListEditorView;

import java.util.List;
import java.util.Map;

public class GuiShoppingListEditorView implements ShoppingListEditorView {

    @Override
    public void setController(ShoppingListEditorController controller) {

    }

    @Override
    public void start(){

    }

    @Override
    public void showCurrentList(ShoppingList list) {

    }

    @Override
    public void showDietStatus(Map<AppCategory, String> demandsStatus){

    }

    @Override
    public void showCatalogProducts(AppCategory category, List<CommercialProduct> products){

    }

    @Override
    public int askQuantityWithAdvice(CommercialProduct product, String suggestionMsg, int suggestedQty){
        return 0;
    }

    @Override
    public boolean showUnmetDemandsWarning(List<String> missingCategories){
        return false;
    }

    @Override
    public void showMessage(String msg){

    }

    @Override
    public void showError(String err){

    }

    @Override
    public void close(){

    }

}
