package models.factory;

import view.*;

public class GuiViewFactory implements ViewFactory {
    @Override
    public AuthView createAuthView() { return new GuiAuthView(); }

    @Override
    public PatientHomeView createPatientHomeView() {return new GuiPatientHomeView(); }

    @Override
    public DietViewerView createDietViewerView() {return new GuiDietViewerView(); }

    @Override
    public NutritionistHomeView createNutritionistHomeView() {return new GuiNutritionistHomeView(); }





}
