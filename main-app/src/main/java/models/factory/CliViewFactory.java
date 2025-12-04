package models.factory;

import view.*;

public class CliViewFactory implements ViewFactory {

    @Override
    public AuthView createAuthView() { return new CliAuthView(); }

    @Override
    public PatientHomeView createPatientHomeView() { return new CliPatientHomeView(); }

    @Override
    public DietViewerView createDietViewerView(){ return new CliDietViewerView(); }




}
