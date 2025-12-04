package models.factory;

import view.AuthView;
import view.DietViewerView;
import view.NutritionistHomeView;
import view.PatientHomeView;

public interface ViewFactory {
    AuthView createAuthView();
    PatientHomeView createPatientHomeView();
    DietViewerView createDietViewerView();
    NutritionistHomeView createNutritionistHomeView();
}
