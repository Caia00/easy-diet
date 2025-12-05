package view;

import controller.DietEditorController;

public interface DietEditorView {
    void setController(DietEditorController controller);

    void start();

    void showMessage(String msg);
    void showError(String err);
    void close();
}
