package view.gui;

import controller.ShoppingListEditorController;
import models.AppCategory;
import models.CommercialProduct;
import models.ShoppingItem;
import models.ShoppingList;
import view.ShoppingListEditorView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;


import java.util.Optional;

import java.util.List;
import java.util.Map;

public class GuiShoppingListEditorView implements ShoppingListEditorView {

    private ShoppingListEditorController controller;
    private final Stage stage;

    private VBox statusBox = new VBox(5);

    private final ObservableList<CommercialProduct> catalogData = FXCollections.observableArrayList();

    private final ObservableList<ShoppingItem> cartData = FXCollections.observableArrayList();

    public GuiShoppingListEditorView(Stage owner) {
        this.stage = new Stage();
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
    }

    @Override
    public void setController(ShoppingListEditorController controller) {
        this.controller = controller;
    }


    @Override
    public void start() {
        stage.setTitle("Editor Lista Spesa Assistita");

        BorderPane root = new BorderPane();

        //Sinistra, monitoraggio lista basato su dieta
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(250);
        leftPane.setStyle("-fx-background-color: #F0F4F8; -fx-border-color: #DDD; -fx-border-width: 0 1 0 0;");

        Label lblStatus = new Label("Copertura Dieta");
        lblStatus.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        //statusBox viene riempito automaticamente da showDietStatus
        ScrollPane statusScroll = new ScrollPane(statusBox);
        statusScroll.setFitToWidth(true);
        statusScroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(statusScroll, Priority.ALWAYS);

        leftPane.getChildren().addAll(lblStatus, new Separator(), statusScroll);
        root.setLeft(leftPane);

        //Centro, divisione: in alto catalogo filtrato, in basso carrello
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        //Catalogo
        VBox catalogPane = new VBox(10);
        catalogPane.setPadding(new Insets(10));

        Label lblCat = new Label("1. Scegli dal Catalogo");
        lblCat.setStyle("-fx-font-weight: bold; -fx-text-fill: #000000;");

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        //Scelta categoria con ComboBox
        ComboBox<AppCategory> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll(AppCategory.values());
        cmbCategory.setPromptText("Filtra per Categoria...");
        cmbCategory.setOnAction(e -> {
            if (cmbCategory.getValue() != null) {
                controller.selectCategory(cmbCategory.getValue());
            }
        });

        filterBox.getChildren().addAll(new Label("Categoria:"), cmbCategory);

        //Tabella catalogo filtrato
        TableView<CommercialProduct> catalogTable = new TableView<>();
        catalogTable.setItems(catalogData);
        catalogTable.setPlaceholder(new Label("Seleziona una categoria per vedere i prodotti."));
        catalogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        //Prodotto
        TableColumn<CommercialProduct, String> colProdName = new TableColumn<>("Prodotto");
        colProdName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        //Peso
        TableColumn<CommercialProduct, String> colProdWeight = new TableColumn<>("Peso");
        colProdWeight.setCellValueFactory(c -> new SimpleStringProperty((int)c.getValue().getWeightInGrams() + "g"));

        //Prezzo
        TableColumn<CommercialProduct, String> colProdPrice = new TableColumn<>("Prezzo");
        colProdPrice.setCellValueFactory(c -> new SimpleStringProperty(String.format("€ %.2f", c.getValue().getPrice())));

        //Azione scegli, creo una classe apposta per gestire l'action cell
        TableColumn<CommercialProduct, Void> colAddAction = new TableColumn<>("Azione");
        colAddAction.setCellFactory(param -> new CatalogActionCell());

        catalogTable.getColumns().addAll(colProdName, colProdWeight, colProdPrice, colAddAction);
        VBox.setVgrow(catalogTable, Priority.ALWAYS);
        catalogPane.getChildren().addAll(lblCat, filterBox, catalogTable);


        //Carrello
        VBox cartPane = new VBox(10);
        cartPane.setPadding(new Insets(10));

        Label lblCart = new Label("2. La tua Lista");
        lblCart.setStyle("-fx-font-weight: bold; -fx-text-fill: #000000;");

        //Tabella di visualizzazione
        TableView<ShoppingItem> cartTable = new TableView<>();
        cartTable.setItems(cartData);
        cartTable.setPlaceholder(new Label("Lista vuota. Aggiungi prodotti dal catalogo sopra."));
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        //Nome prod.
        TableColumn<ShoppingItem, String> cItemName = new TableColumn<>("Prodotto");
        cItemName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));

        //Quantità
        TableColumn<ShoppingItem, String> cItemQty = new TableColumn<>("Qtà");
        cItemQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));

        //Totale
        TableColumn<ShoppingItem, String> cItemTot = new TableColumn<>("Totale");
        cItemTot.setCellValueFactory(c -> new SimpleStringProperty(String.format("€ %.2f", c.getValue().getTotalPrice())));

        //Azione diminuisci o rimuovi
        TableColumn<ShoppingItem, Void> cItemRem = new TableColumn<>("Rimuovi");
        cItemRem.setCellFactory(param -> new CartActionCell());

        cartTable.getColumns().addAll(cItemName, cItemQty, cItemTot, cItemRem);
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        cartPane.getChildren().addAll(lblCart, cartTable);

        splitPane.getItems().addAll(catalogPane, cartPane);
        splitPane.setDividerPositions(0.45);
        root.setCenter(splitPane);

        //In basso, azioni finali
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: #EEE; -fx-border-color: #CCC; -fx-border-width: 1 0 0 0;");

        //Annulla
        Button btnCancel = new Button("Annulla");
        btnCancel.setStyle(GuiTheme.BTN_SECONDARY_STYLE);
        btnCancel.setOnAction(e -> controller.cancel());

        //Salva
        Button btnSave = new Button("Salva Lista e Esci");
        btnSave.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnSave.setPrefWidth(200);
        btnSave.setOnAction(e -> controller.saveAndExit());

        bottomBar.getChildren().addAll(btnCancel, btnSave);
        root.setBottom(bottomBar);
        
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.showAndWait();
    }


    @Override
    public void showCurrentList(ShoppingList list) {
        cartData.setAll(list.getItems());
    }


    @Override
    public void showDietStatus(Map<AppCategory, String> demandsStatus) {
        statusBox.getChildren().clear();

        for (Map.Entry<AppCategory, String> entry : demandsStatus.entrySet()) {
            AppCategory cat = entry.getKey();
            String val = entry.getValue();

            HBox row = new HBox(10);
            row.setPadding(new Insets(5));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: #DDD; -fx-border-radius: 5; -fx-background-color: white;");

            Label lblName = new Label(cat.toString());
            lblName.setStyle("-fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblVal = new Label(val);

            //Check di X/Y per determinarne il colore
            try {
                String[] parts = val.split("/");
                long covered = Long.parseLong(parts[0]);
                long total = Long.parseLong(parts[1]);

                if (covered >= total) {
                    lblVal.setTextFill(Color.GREEN);
                    row.setStyle(row.getStyle() + "-fx-border-color: green; -fx-border-width: 0 0 0 3;");
                } else {
                    lblVal.setTextFill(Color.RED);
                    row.setStyle(row.getStyle() + "-fx-border-color: red; -fx-border-width: 0 0 0 3;");
                }
            } catch (Exception _) { /* Ignoro errori di parsing */ }

            row.getChildren().addAll(lblName, spacer, lblVal);
            statusBox.getChildren().add(row);
        }
    }

    @Override
    public void showCatalogProducts(AppCategory category, List<CommercialProduct> products) {
        catalogData.setAll(products);
    }

    //Metodo per richiesta quantità durante l'aggiunta di un prodotto al carrello mediante un dialog
    @Override
    public int askQuantityWithAdvice(CommercialProduct product, String suggestionMsg, int suggestedQty) {

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Quanti ne vuoi acquistare?");
        dialog.setHeaderText("Prodotto: " + product.getName());

        ButtonType btnOk = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        //Consiglio del sistema
        Label lblAdvice = new Label(suggestionMsg);
        lblAdvice.setWrapText(true);
        lblAdvice.setMaxWidth(400);

        if (suggestionMsg.startsWith("DIETA")) {
            lblAdvice.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32; -fx-font-size: 13px;");
        } else {
            lblAdvice.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        }

        //Input quantità con spinner
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        Label lblQ = new Label("Quantità (confezioni):");

        Spinner<Integer> spinner = new Spinner<>(1, 100, suggestedQty);
        spinner.setEditable(true);

        inputBox.getChildren().addAll(lblQ, spinner);

        content.getChildren().addAll(lblAdvice, new Separator(), inputBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnOk) {
                return spinner.getValue();
            }
            return 0;
        });

        Optional<Integer> result = dialog.showAndWait();
        return result.orElse(0);
    }

    @Override
    public boolean showUnmetDemandsWarning(List<String> missingCategories) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attenzione: Dieta incompleta");
        alert.setHeaderText("Alcuni pasti della tua dieta non sono ancora coperti.");

        StringBuilder sb = new StringBuilder("Categorie mancanti:\n");
        for (String cat : missingCategories) {
            sb.append("- ").append(cat).append("\n");
        }
        sb.append("\nVuoi salvare comunque la lista?");

        alert.setContentText(sb.toString());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
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
    public void close() {
        stage.close();
    }

    private class CatalogActionCell extends TableCell<CommercialProduct, Void> {
        private final Button btnAdd = new Button("Scegli");

        public CatalogActionCell() {
            btnAdd.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
            btnAdd.setOnAction(e -> {
                CommercialProduct p = getTableView().getItems().get(getIndex());
                controller.selectProduct(p);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btnAdd);
        }
    }


    private class CartActionCell extends TableCell<ShoppingItem, Void> {
        private final Button btnMinus = new Button("-1");
        private final Button btnTrash = new Button("X");
        private final HBox box = new HBox(5, btnMinus, btnTrash);

        public CartActionCell() {
            box.setAlignment(Pos.CENTER);
            btnMinus.setOnAction(e -> controller.removeProduct(getIndex(), 1));

            btnTrash.setStyle(GuiTheme.BTN_DANGER_STYLE);
            btnTrash.setOnAction(e -> controller.removeProduct(getIndex(), 9999));
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : box);
        }
    }

}
