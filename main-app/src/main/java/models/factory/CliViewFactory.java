package models.factory;

import view.*;
import view.cli.*;

public class CliViewFactory implements ViewFactory {

    @Override
    public AuthView createAuthView() { return new CliAuthView(); }

    @Override
    public PatientHomeView createPatientHomeView() { return new CliPatientHomeView(); }

    @Override
    public DietViewerView createDietViewerView(){ return new CliDietViewerView(); }

    @Override
    public NutritionistHomeView createNutritionistHomeView(){ return new CliNutritionistHomeView(); }

    @Override
    public DietManagerView createDietManagerView(){ return new CliDietManagerView(); }

    @Override
    public DietEditorView createDietEditorView(){ return new CliDietEditorView(); }

    @Override
    public ShoppingListManagerView createShoppingListManagerView(){ return new CliShoppingListManagerView(); }

    @Override
    public ShoppingListEditorView createShoppingListEditorView(){ return new CliShoppingListEditorView(); }






}
