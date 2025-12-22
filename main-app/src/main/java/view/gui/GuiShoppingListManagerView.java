package view.gui;

import controller.ShoppingListManagerController;
import models.*;
import view.ShoppingListManagerView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

import java.time.format.DateTimeFormatter;

import java.util.List;

public class GuiShoppingListManagerView implements ShoppingListManagerView {
    private ShoppingListManagerController controller;
    private final Stage stage;
    private final ObservableList<ShoppingList> historyData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public GuiShoppingListManagerView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setController(ShoppingListManagerController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        stage.setTitle("Le mie Liste della Spesa");

        BorderPane root = new BorderPane();
        root.setTop(GuiTheme.createHeader("Le mie Liste", () -> controller.back(), false));

        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(15));

        //Barra creazione
        HBox createBar = new HBox(10);
        createBar.setAlignment(Pos.CENTER_LEFT);
        createBar.setPadding(new Insets(10));
        createBar.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 5; -fx-border-color: #DDD; -fx-border-radius: 5;");

        TextField txtName = new TextField();
        txtName.setPromptText("Nome lista (es. Spesa Lunedì)");
        txtName.setPrefWidth(200);

        ComboBox<SupermarketName> cmbMarket = new ComboBox<>();
        cmbMarket.getItems().addAll(SupermarketName.values());
        cmbMarket.setValue(SupermarketName.CARREFOUR);

        Button btnCreate = new Button("Crea Nuova Lista");
        btnCreate.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnCreate.setOnAction(e -> {
            if (cmbMarket.getValue() != null) {
                controller.createNewList(txtName.getText(), cmbMarket.getValue());
                txtName.clear();
            } else {
                showError("Seleziona un supermercato.");
            }
        });

        createBar.getChildren().addAll(new Label("Nuova Lista:"), txtName, cmbMarket, btnCreate);

        //Tabella con liste della spesa create
        TableView<ShoppingList> table = new TableView<>();
        table.setItems(historyData);
        table.setPlaceholder(new Label("Non hai ancora creato liste della spesa."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(table, Priority.ALWAYS);

        //Colonne nella tabella:
        //Nome
        TableColumn<ShoppingList, String> colName = new TableColumn<>("Nome Lista");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getListName()));

        //Supermercato
        TableColumn<ShoppingList, String> colMarket = new TableColumn<>("Supermercato");
        colMarket.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSupermarket().toString()));

        //Data creazione
        TableColumn<ShoppingList, String> colDate = new TableColumn<>("Data");
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCreationDate().format(DATE_FMT)
        ));

        //Num prodotti
        TableColumn<ShoppingList, String> colCount = new TableColumn<>("Prodotti");
        colCount.setCellValueFactory(cell -> new SimpleStringProperty(
                String.valueOf(cell.getValue().getTotalItemsCount())
        ));
        colCount.setStyle("-fx-alignment: CENTER;");

        //Costo totale
        TableColumn<ShoppingList, String> colCost = new TableColumn<>("Totale");
        colCost.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("€ %.2f", cell.getValue().getTotalCost())
        ));
        colCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        //Azioni
        TableColumn<ShoppingList, Void> colActions = new TableColumn<>("Azioni");
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnOpen = new Button("Dettagli");
            private final Button btnDel = new Button("Elimina");
            private final HBox pane = new HBox(10, btnOpen, btnDel);

            {
                pane.setAlignment(Pos.CENTER);
                btnOpen.setStyle("-fx-background-color: " + GuiTheme.COL_SECONDARY + "; -fx-text-fill: white; -fx-cursor: hand;");
                btnOpen.setOnAction(e -> {
                    ShoppingList list = getTableView().getItems().get(getIndex());
                    controller.openList(list);
                });

                btnDel.setStyle(GuiTheme.BTN_DANGER_STYLE);
                btnDel.setOnAction(e -> {
                    ShoppingList list = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare '" + list.getListName() + "'?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.YES) controller.deleteList(list);
                    });
                });
            }

            //Metodo usato automaticamente per il riutilizzo delle righe
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(colName, colMarket, colDate, colCount, colCost, colActions);

        centerBox.getChildren().addAll(createBar, table);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 950, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void showShoppingHistory(List<ShoppingList> history) {
        historyData.setAll(history);
    }

    //Per mostrare gli oggetti contenuti in una lista uso un popup
    @Override
    public void showListDetails(ShoppingList list) {
        Stage detailStage = new Stage();
        detailStage.setTitle("Dettaglio: " + list.getListName());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        //Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label(list.getListName());
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblTotal = new Label(String.format("Totale: € %.2f", list.getTotalCost()));
        lblTotal.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        header.getChildren().addAll(lblTitle, lblTotal);

        //Tabella (lista) prodotti
        TableView<ShoppingItem> detailTable = new TableView<>();
        detailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        ObservableList<ShoppingItem> itemsData = FXCollections.observableArrayList(list.getItems());
        detailTable.setItems(itemsData);

        //Prodotto
        TableColumn<ShoppingItem, String> cName = new TableColumn<>("Prodotto");
        cName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));

        //Quantità
        TableColumn<ShoppingItem, String> cQty = new TableColumn<>("Qtà");
        cQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        cQty.setStyle("-fx-alignment: CENTER;");

        //Prezzo prodotto
        TableColumn<ShoppingItem, String> cPrice = new TableColumn<>("Prezzo prod.");
        cPrice.setCellValueFactory(c -> new SimpleStringProperty(String.format("€ %.2f", c.getValue().getProduct().getPrice())));

        //Prezzo * Quantità
        TableColumn<ShoppingItem, String> cTot = new TableColumn<>("Totale");
        cTot.setCellValueFactory(c -> new SimpleStringProperty(String.format("€ %.2f", c.getValue().getTotalPrice())));
        cTot.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");

        //Info
        TableColumn<ShoppingItem, Void> cInfo = new TableColumn<>("Note");
        cInfo.setCellFactory(param -> new TableCell<>() {


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    ShoppingItem currentItem = (ShoppingItem) getTableRow().getItem();
                    if (currentItem.isForDiet()) {
                        Label lbl = new Label("Per Dieta");
                        lbl.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 10px;");
                        setGraphic(lbl);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        detailTable.getColumns().addAll(cName, cQty, cPrice, cTot, cInfo);
        detailTable.setPlaceholder(new Label("Lista vuota."));
        VBox.setVgrow(detailTable, Priority.ALWAYS);

        Button btnClose = new Button("Chiudi");
        btnClose.setStyle(GuiTheme.BTN_SECONDARY_STYLE);
        btnClose.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(header, detailTable, btnClose);

        Scene scene = new Scene(layout, 600, 500);
        detailStage.setScene(scene);
        detailStage.show();
    }

    @Override
    public void showMessage(String msg) {
        Toast.showSuccess(stage, msg);
    }

    @Override
    public void showError(String err) {
        Toast.showError(stage, err);
    }

    @Override
    public void close() {}

}
