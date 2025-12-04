package models.factory;

import view.AuthView;
import view.DietViewerView;
import view.PatientHomeView;

public interface ViewFactory {
    AuthView createAuthView();
    PatientHomeView createPatientHomeView();
    DietViewerView createDietViewerView();
}
