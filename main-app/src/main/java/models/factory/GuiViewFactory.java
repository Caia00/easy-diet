package models.factory;

import javafx.stage.Stage;
import view.*;
import view.gui.*;

public class GuiViewFactory implements ViewFactory {

    private final Stage primaryStage;

    public GuiViewFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @Override
    public AuthView createAuthView() { return new GuiAuthView(primaryStage); }

    @Override
    public PatientHomeView createPatientHomeView() {return new GuiPatientHomeView(primaryStage); }

    @Override
    public DietViewerView createDietViewerView() {return new GuiDietViewerView(); }

    @Override
    public NutritionistHomeView createNutritionistHomeView() {return new GuiNutritionistHomeView(primaryStage); }

    @Override
    public DietManagerView createDietManagerView() {return new GuiDietManagerView(); }

    @Override
    public DietEditorView createDietEditorView() {return new GuiDietEditorView(); }

    @Override
    public ShoppingListManagerView createShoppingListManagerView() {return new GuiShoppingListManagerView(); }

    @Override
    public ShoppingListEditorView createShoppingListEditorView() {return new GuiShoppingListEditorView(); }






}
