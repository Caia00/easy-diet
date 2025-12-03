package view;

import controller.PatientHomeController;

public interface PatientHomeView {
    void setController(PatientHomeController controller);

    void start();

    void showWelcomeMessage(String userName);

    void showErrorMessage(String message);

    void showSuccessMessage(String message);

    void close();
}
