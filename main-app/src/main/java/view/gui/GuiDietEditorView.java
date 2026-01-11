package view.gui;

import controller.DietEditorController;
import models.AppCategory;
import models.beans.FoodBean;
import view.DietEditorView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

public class GuiDietEditorView implements DietEditorView {

    private DietEditorController controller;
    private final Stage editorStage;
    private static final String[] DAYS = {
            "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"
    };
    private static final String style = "-fx-font-weight: bold; -fx-font-size: 14px;";
    private static final String giorno = "Giorno:";

    public GuiDietEditorView(Stage owner) {
        this.editorStage = new Stage();
        this.editorStage.initOwner(owner);
        this.editorStage.initModality(Modality.WINDOW_MODAL); //Blocco lo stage owner (primaryStage) finché questo lavora
    }

    @Override
    public void setController(DietEditorController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        editorStage.setTitle("Editor Dieta");
        BorderPane root = new BorderPane();

        //Top
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(15));
        topContainer.setStyle("-fx-background-color: #EEE; -fx-border-color: #CCC; -fx-border-width: 0 0 1 0;");

        HBox renameBox = new HBox(10);
        renameBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtDietName = new TextField();
        txtDietName.setPromptText("Nuovo nome dieta");
        Button btnRename = new Button("Rinomina Dieta");
        btnRename.setStyle(GuiTheme.BTN_SECONDARY_STYLE);
        btnRename.setOnAction(e -> controller.renameDiet(txtDietName.getText()));

        renameBox.getChildren().addAll(new Label("Nome Dieta:"), txtDietName, btnRename);
        topContainer.getChildren().add(renameBox);
        root.setTop(topContainer);

        //Centro
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); //Non permetto di chiudere le tab

        Tab tabMeals = new Tab("Gestione Pasti");
        tabMeals.setContent(createMealManagementTab());

        Tab tabAddFood = new Tab("Aggiungi Alimenti");
        tabAddFood.setContent(createAddFoodTab());

        Tab tabRemoveFood = new Tab("Rimuovi Alimenti");
        tabRemoveFood.setContent(createRemoveFoodTab());

        tabPane.getTabs().addAll(tabMeals, tabAddFood, tabRemoveFood);
        root.setCenter(tabPane);

        //Bottom
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: #EEE; -fx-border-color: #CCC; -fx-border-width: 1 0 0 0;");

        Button btnCancel = new Button("Annulla / Chiudi");
        btnCancel.setStyle(GuiTheme.BTN_DANGER_STYLE);
        btnCancel.setOnAction(e -> controller.cancel());

        Button btnSave = new Button("Salva e Esci");
        btnSave.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnSave.setPrefWidth(150);
        btnSave.setOnAction(e -> controller.saveAndExit());
        bottomBar.getChildren().addAll(btnCancel, btnSave);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 600, 700);
        editorStage.setScene(scene);

        editorStage.showAndWait(); //Blocchiamo il DietManagerController finché si edita
    }


    private VBox createMealManagementTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        //Aggiunta pasto
        Label lblAdd = new Label("Aggiungi Nuovo Pasto");
        lblAdd.setStyle(style);

        GridPane gridAdd = new GridPane();
        gridAdd.setHgap(10); gridAdd.setVgap(10);

        ComboBox<String> cmbDayAdd = new ComboBox<>();
        cmbDayAdd.getItems().addAll(DAYS);
        cmbDayAdd.setPromptText("Giorno");

        TextField txtMealNameAdd = new TextField();
        txtMealNameAdd.setPromptText("Nome (es. Colazione)");

        TextField txtTimeAdd = new TextField();
        txtTimeAdd.setPromptText("Orario (HH:mm)");

        Button btnAddMeal = new Button("Aggiungi Pasto");
        btnAddMeal.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAddMeal.setOnAction(e -> controller.addMeal(
                cmbDayAdd.getValue(), txtMealNameAdd.getText(), txtTimeAdd.getText()
        ));

        gridAdd.addRow(0, new Label(giorno), cmbDayAdd);
        gridAdd.addRow(1, new Label("Nome:"), txtMealNameAdd);
        gridAdd.addRow(2, new Label("Ora:"), txtTimeAdd);
        gridAdd.add(btnAddMeal, 1, 3);

        //Rimozione pasto
        Separator sep = new Separator();

        Label lblRem = new Label("Rimuovi Intero Pasto");
        lblRem.setStyle(style);

        GridPane gridRem = new GridPane();
        gridRem.setHgap(10); gridRem.setVgap(10);

        ComboBox<String> cmbDayRem = new ComboBox<>();
        cmbDayRem.getItems().addAll(DAYS);
        cmbDayRem.setPromptText("Giorno");

        TextField txtMealNameRem = new TextField();
        txtMealNameRem.setPromptText("Nome pasto da rimuovere");

        Button btnRemMeal = new Button("Rimuovi Pasto");
        btnRemMeal.setStyle(GuiTheme.BTN_DANGER_STYLE);
        btnRemMeal.setOnAction(e -> controller.removeMeal(
                cmbDayRem.getValue(), txtMealNameRem.getText()
        ));

        gridRem.addRow(0, new Label(giorno), cmbDayRem);
        gridRem.addRow(1, new Label("Nome:"), txtMealNameRem);
        gridRem.add(btnRemMeal, 1, 2);
        layout.getChildren().addAll(lblAdd, gridAdd, new Label(""), sep, lblRem, gridRem);
        return layout;
    }


    private ScrollPane createAddFoodTab() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Inserisci Alimento nel Pasto");
        title.setStyle(style);

        //Dove verrà aggiunto
        ComboBox<String> cmbDay = new ComboBox<>();
        cmbDay.getItems().addAll(DAYS);
        cmbDay.setPromptText("Seleziona Giorno");

        TextField txtMealName = new TextField();
        txtMealName.setPromptText("Nome Pasto (es. Pranzo)");

        //Cosa verrà aggiunto
        ComboBox<AppCategory> cmbCat = new ComboBox<>();
        cmbCat.getItems().addAll(AppCategory.values());
        cmbCat.setPromptText("Categoria Alimento");

        TextField txtProduct = new TextField();
        txtProduct.setPromptText("Prodotto Suggerito (Opzionale)");

        //Nutrienti
        Label lblMacro = new Label("Valori nutrizionali del cibo:");
        lblMacro.setStyle("-fx-font-weight: bold; margin-top: 10px;");

        GridPane macroGrid = new GridPane();
        macroGrid.setHgap(10); macroGrid.setVgap(10);

        TextField txtKcal = new TextField("0");
        TextField txtProt = new TextField("0");
        TextField txtCarb = new TextField("0");
        TextField txtFat = new TextField("0");
        TextField txtSug = new TextField("0");
        TextField txtFib = new TextField("0");

        addMacroField(macroGrid, "Kcal:", txtKcal, 0, 0);
        addMacroField(macroGrid, "Proteine:", txtProt, 0, 1);
        addMacroField(macroGrid, "Carboidrati:", txtCarb, 1, 0);
        addMacroField(macroGrid, "Grassi:", txtFat, 1, 1);
        addMacroField(macroGrid, "Zuccheri:", txtSug, 2, 0);
        addMacroField(macroGrid, "Fibre:", txtFib, 2, 1);

        Button btnAdd = new Button("Aggiungi Alimento");
        btnAdd.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdd.setPrefWidth(200);

        btnAdd.setOnAction(e -> {
            try {
                FoodBean bean = new FoodBean(cmbDay.getValue(),
                        txtMealName.getText(),
                        cmbCat.getValue(),
                        Double.parseDouble(txtKcal.getText()),
                        Double.parseDouble(txtProt.getText()),
                        Double.parseDouble(txtCarb.getText()),
                        Double.parseDouble(txtSug.getText()));
                bean.setFat(Double.parseDouble(txtFat.getText()));
                bean.setFib(Double.parseDouble(txtFib.getText()));
                bean.setSuggestedProductName(txtProduct.getText());
                controller.addFoodItem(bean);
            } catch (NumberFormatException _) {
                showError("I valori nutrizionali devono essere numeri (usa il punto).");
            } catch (Exception _) {
                showError("Compila tutti i campi obbligatori.");
            }
        });

        layout.getChildren().addAll(
                title,
                new Label(giorno), cmbDay,
                new Label("Pasto Target:"), txtMealName,
                new Separator(),
                new Label("Categoria:"), cmbCat,
                new Label("Prodotto (Opz.):"), txtProduct,
                lblMacro, macroGrid,
                new Label(""), btnAdd
        );

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private void addMacroField(GridPane grid, String label, TextField field, int col, int row) {
        grid.add(new Label(label), col * 2, row);
        grid.add(field, col * 2 + 1, row);
        field.setPrefWidth(80);
    }

    private VBox createRemoveFoodTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        Label info = new Label("Per rimuovere un alimento, guarda l'indice (numero d'ordine) nella finestra della dieta.");
        info.setWrapText(true);

        ComboBox<String> cmbDay = new ComboBox<>();
        cmbDay.getItems().addAll(DAYS);

        TextField txtMeal = new TextField();
        txtMeal.setPromptText("Nome Pasto");

        TextField txtIndex = new TextField();
        txtIndex.setPromptText("Indice (0 = primo, 1 = secondo...)");

        Button btnRemove = new Button("Rimuovi Alimento");
        btnRemove.setStyle(GuiTheme.BTN_DANGER_STYLE);
        btnRemove.setOnAction(e -> {
            try {
                int index = Integer.parseInt(txtIndex.getText());
                controller.removeFoodItem(cmbDay.getValue(), txtMeal.getText(), index);
            } catch (NumberFormatException _) {
                showError("L'indice deve essere un numero intero.");
            }
        });

        layout.getChildren().addAll(info, new Label(giorno), cmbDay, new Label("Pasto:"), txtMeal, new Label("Indice:"), txtIndex, btnRemove);
        return layout;
    }


    @Override
    public void showMessage(String msg) {
        Toast.showSuccess(editorStage, msg);
    }

    @Override
    public void showError(String err) {
        Toast.showError(editorStage, err);
    }

    @Override
    public void close() {
        editorStage.close();
    }
}
