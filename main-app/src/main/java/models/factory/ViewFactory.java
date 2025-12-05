package models.factory;

import view.*;

public interface ViewFactory {
    AuthView createAuthView();
    PatientHomeView createPatientHomeView();
    DietViewerView createDietViewerView();
    NutritionistHomeView createNutritionistHomeView();
    DietManagerView createDietManagerView();
    DietEditorView createDietEditorView();
}
