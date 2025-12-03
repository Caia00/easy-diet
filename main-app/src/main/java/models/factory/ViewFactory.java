package models.factory;

import view.AuthView;
import view.PatientHomeView;

public interface ViewFactory {
    AuthView createAuthView();
    PatientHomeView createPatientHomeView();
}
