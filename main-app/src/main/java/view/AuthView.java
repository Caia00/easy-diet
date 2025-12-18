package view;

import controller.LoginController;

public interface AuthView {
    void setController(LoginController controller);

    void start();

    void switchToLogin();

    void showErrorMessage(String message);

    void showSuccessMessage(String message);

    void close();
}
