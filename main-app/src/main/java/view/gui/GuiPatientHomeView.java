package view.gui;

import controller.PatientHomeController;
import view.PatientHomeView;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

public class GuiPatientHomeView implements PatientHomeView {
    private PatientHomeController controller;
    private final Stage stage;
    private String welcomeText = "Benvenuto Paziente";

    public GuiPatientHomeView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setController(PatientHomeController controller) {
        this.controller = controller;
    }

    @Override
    public void showWelcomeMessage(String userName) {
        this.welcomeText = "Ciao, " + userName + "!";
    }

    @Override
    public void start() {
        BorderPane root = new BorderPane();
        root.setTop(GuiTheme.createHeader("EasyDiet - Paziente", () -> controller.logout(), true));

        VBox menuBox = new VBox(25);
        menuBox.setAlignment(Pos.CENTER);

        Label lblWelcome = new Label(welcomeText);
        lblWelcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label lblSubtitle = new Label("La tua salute a portata di click");
        lblSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");

        Button btnDiet = createMenuButton("La mia Dieta");
        btnDiet.setOnAction(e -> controller.openMyDiet());
        Button btnShop = createMenuButton("Liste della Spesa");
        btnShop.setOnAction(e -> controller.openShoppingList());

        menuBox.getChildren().addAll(lblWelcome, lblSubtitle, new Label(""), btnDiet, btnShop);
        root.setCenter(menuBox);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.showError(stage, message);
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.showSuccess(stage, message);
    }

    @Override
    public void close() {
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + GuiTheme.COL_PRIMARY + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );
        btn.setPrefWidth(280);
        btn.setPrefHeight(60);
        return btn;
    }

}
