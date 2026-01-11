package view.gui;

import controller.DietManagerController;
import models.DietPlan;
import view.DietManagerView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

import java.util.Optional;

import java.util.List;

public class GuiDietManagerView implements DietManagerView {
    private DietManagerController controller;
    private final Stage stage;
    private final ObservableList<DietPlan> dietData = FXCollections.observableArrayList();


    public GuiDietManagerView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setController(DietManagerController controller) {
        this.controller = controller;
    }

    @Override
    public void showDietList(List<DietPlan> summaries) {
        dietData.setAll(summaries);
    }

    @Override
    public void start() {
        stage.setTitle("Gestione Diete - Nutrizionista");

        BorderPane root = new BorderPane();
        root.setTop(GuiTheme.createHeader("Le mie Diete (Template)", () -> controller.back(), false));

        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(15));

        //Creazione nuova dieta
        HBox createBar = new HBox(10);
        createBar.setAlignment(Pos.CENTER_LEFT);
        createBar.setPadding(new Insets(10));
        createBar.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 5;");

        TextField txtNewName = new TextField();
        txtNewName.setPromptText("Nome nuova dieta (es. Iperproteica)");
        txtNewName.setPrefWidth(300);

        Button btnCreate = new Button("Crea Nuova Dieta");
        btnCreate.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnCreate.setOnAction(e -> {
            controller.createDiet(txtNewName.getText());
            txtNewName.clear();
        });

        createBar.getChildren().addAll(new Label("Nuova Dieta:"), txtNewName, btnCreate);

        //Tabella diete
        TableView<DietPlan> table = new TableView<>();
        table.setItems(dietData);
        table.setPlaceholder(new Label("Nessuna dieta creata. Inizia creandone una sopra!"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        //Campo 1, nome
        TableColumn<DietPlan, String> nameCol = new TableColumn<>("Nome Dieta");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDietName()));

        //Campo 2, id
        TableColumn<DietPlan, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getDietId())));
        idCol.setMaxWidth(50);
        idCol.setMinWidth(50);

        //Campo 3, azioni
        TableColumn<DietPlan, Void> actionsCol = new TableColumn<>("Azioni");
        actionsCol.setSortable(false);

        actionsCol.setCellFactory(param -> new ActionCell());

        table.getColumns().addAll(idCol, nameCol, actionsCol);

        centerContent.getChildren().addAll(createBar, table);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void showMessage(String message) {
        Toast.showSuccess(stage, message);
    }

    @Override
    public void showError(String message) {
        Toast.showError(stage, message);
    }

    @Override
    public void close() {
        //Metodo vuoto in quanto la chiusura della gui consisterà semplicemente dal cambiamento della scena sullo stage da parte della nuova gui
    }


    private class ActionCell extends TableCell<DietPlan, Void> {
        private final Button btnEdit = new Button("Modifica");
        private final Button btnAssign = new Button("Assegna");
        private final Button btnDelete = new Button("Elimina");
        private final HBox pane = new HBox(10, btnEdit, btnAssign, btnDelete);

        public ActionCell() {
            pane.setAlignment(Pos.CENTER);

            btnEdit.setStyle("-fx-background-color: " + GuiTheme.COL_PRIMARY + "; -fx-text-fill: white; -fx-cursor: hand;");
            btnEdit.setOnAction(e -> {
                DietPlan plan = getTableView().getItems().get(getIndex());
                controller.editDiet(plan);
            });

            btnAssign.setStyle("-fx-background-color: " + GuiTheme.COL_SECONDARY + "; -fx-text-fill: white; -fx-cursor: hand;");
            btnAssign.setOnAction(e -> {
                DietPlan plan = getTableView().getItems().get(getIndex());
                handleAssignClick(plan);
            });

            btnDelete.setStyle(GuiTheme.BTN_DANGER_STYLE);
            btnDelete.setOnAction(e -> {
                DietPlan plan = getTableView().getItems().get(getIndex());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Sei sicuro di voler eliminare la dieta '" + plan.getDietName() + "'?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) controller.deleteDiet(plan);
                });
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : pane);
        }

        private void handleAssignClick(DietPlan plan) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Assegna Dieta");
            dialog.setHeaderText("Assegna '" + plan.getDietName() + "' a un paziente");
            dialog.setContentText("Inserisci l'email del paziente:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(email ->
                    controller.assignDiet(plan, email));
        }
    }
}
