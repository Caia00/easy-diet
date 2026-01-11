package view.gui;

import controller.NutritionistHomeController;
import view.NutritionistHomeView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

public class GuiNutritionistHomeView implements NutritionistHomeView {
    private NutritionistHomeController controller;
    private final Stage stage;
    private String welcomeName = "Dottore";

    public GuiNutritionistHomeView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setController(NutritionistHomeController controller) {
        this.controller = controller;
    }

    @Override
    public void showWelcome(String name) {
        this.welcomeName = "Dr. " + name;
    }

    @Override
    public void start() {
        BorderPane root = new BorderPane();
        stage.setTitle("EasyDiet - Portale Nutrizionista");

        root.setTop(GuiTheme.createHeader("EasyDiet - Nutrizionista", () -> controller.logout(), true));
        VBox menuBox = new VBox(25);
        menuBox.setAlignment(Pos.CENTER);

        Label lblWelcome = new Label("Benvenuto, " + welcomeName);
        lblWelcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Label lblSubtitle = new Label("Gestione Pazienti e Piani Alimentari");
        lblSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");

        Button btnDietManager = createMenuButton("Gestione Diete Pazienti");
        btnDietManager.setOnAction(e -> controller.goToDietManager());
        Button btnProfile = createMenuButton("Il mio Profilo");
        btnProfile.setOnAction(e -> controller.goToProfile());

        menuBox.getChildren().addAll(lblWelcome, lblSubtitle, new Label(""), btnDietManager, btnProfile);
        root.setCenter(menuBox);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void showError(String err) {
        Toast.showError(stage, err);
    }

    @Override
    public void close() {
        //Metodo vuoto in quanto la chiusura della gui consisterà nel cambio della scena da parte della nuova gui
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
        btn.setPrefWidth(300); // Leggermente più largo
        btn.setPrefHeight(60);
        btn.setOnMouseEntered(e -> btn.setOpacity(0.9));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        return btn;
    }
}
