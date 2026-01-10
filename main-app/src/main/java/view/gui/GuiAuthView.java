package view.gui;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.beans.ProfileBean;
import view.AuthView;
import view.gui.utility.GuiTheme;
import view.gui.utility.Toast;

public class GuiAuthView implements AuthView {

    private LoginController controller;
    private final Stage stage;
    private BorderPane rootLayout;
    private final String email1 = "Email";
    private final String email2 = "Email:";
    private final String password1 = "Password";
    private final String password2 = "Password:";
    private final String paziente = "Paziente";

    public GuiAuthView(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setController(LoginController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        stage.setTitle("EasyDiet - Accesso");
        rootLayout = new BorderPane();
        showLoginScreen();

        Scene scene = new Scene(rootLayout, 500, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void showLoginScreen() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Benvenuto in EasyDiet");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText(email1);

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText(password1);

        Button btnLogin = new Button("Accedi");
        btnLogin.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnLogin.setPrefWidth(200);

        btnLogin.setOnAction(e -> {
            if (controller != null) {
                controller.login(txtEmail.getText(), txtPassword.getText());
            }
        });

        Hyperlink linkRegister = new Hyperlink("Non hai un account? Registrati qui");
        linkRegister.setOnAction(e -> showRegisterScreen());
        content.getChildren().addAll(lblTitle, new Label(email2), txtEmail, new Label(password2), txtPassword, btnLogin, linkRegister);
        rootLayout.setCenter(content);
    }


    private void showRegisterScreen() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label lblTitle = new Label("Crea un nuovo account");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        //Combo box per scegliere come registrarsi e avere il form corretto da compilare
        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(paziente, "Nutrizionista");
        cmbType.setValue(paziente);//Visualizzo prima paziente

        VBox dynamicForm = new VBox(10);
        dynamicForm.setAlignment(Pos.TOP_LEFT);

        renderPatientForm(dynamicForm);

        cmbType.setOnAction(e -> {
            dynamicForm.getChildren().clear();
            if (cmbType.getValue().equals(paziente)) {
                renderPatientForm(dynamicForm);
            } else {
                renderNutritionistForm(dynamicForm);
            }
        });

        Hyperlink linkLogin = new Hyperlink("Hai già un account? Accedi");
        linkLogin.setOnAction(e -> showLoginScreen());

        //Scroll pane utilizzato per contenere i form
        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getChildren().addAll(lblTitle, new Label("Sono un:"), cmbType, dynamicForm, linkLogin);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        rootLayout.setCenter(scrollPane);
    }

    //Form paziente
    private void renderPatientForm(VBox container) {
        TextField txtName = new TextField(); txtName.setPromptText("Nome");
        TextField txtSurname = new TextField(); txtSurname.setPromptText("Cognome");
        TextField txtEmail = new TextField(); txtEmail.setPromptText(email1);
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText(password1);
        DatePicker dateBirth = new DatePicker(); dateBirth.setPromptText("Data di Nascita");
        TextField txtHeight = new TextField(); txtHeight.setPromptText("Altezza (cm)");
        TextField txtWeight = new TextField(); txtWeight.setPromptText("Peso (kg)");

        ComboBox<String> cmbGender = new ComboBox<>();
        cmbGender.getItems().addAll("Uomo", "Donna", "Altro");
        cmbGender.setPromptText("Genere");

        Button btnRegister = new Button("Registra Paziente");
        btnRegister.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnRegister.setPrefWidth(200);

        btnRegister.setOnAction(e -> {
            try {
                double h = Double.parseDouble(txtHeight.getText());
                double w = Double.parseDouble(txtWeight.getText());

                ProfileBean bean = new ProfileBean(txtName.getText(), txtSurname.getText(), txtEmail.getText(), txtPass.getText(), dateBirth.getValue());

                controller.registerPatient(bean, h, w, cmbGender.getValue()
                );
            } catch (NumberFormatException _) {
                showErrorMessage("Altezza e Peso devono essere numeri (usa il punto per i decimali).");
            } catch (Exception _) {
                showErrorMessage("Dati mancanti o non validi.");
            }
        });


        container.getChildren().addAll(
                new Label("Nome:"), txtName,
                new Label("Cognome:"), txtSurname,
                new Label(email2), txtEmail,
                new Label(password2), txtPass,
                new Label("Data Nascita:"), dateBirth,
                new Label("Altezza (cm):"), txtHeight,
                new Label("Peso (kg):"), txtWeight,
                new Label("Genere:"), cmbGender,
                new Label(""),
                btnRegister
        );
    }

    //Form nutrizionista
    private void renderNutritionistForm(VBox container) {
        TextField txtName = new TextField(); txtName.setPromptText("Nome");
        TextField txtSurname = new TextField(); txtSurname.setPromptText("Cognome");
        TextField txtEmail = new TextField(); txtEmail.setPromptText(email1);
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText(password1);
        DatePicker dateBirth = new DatePicker(); dateBirth.setPromptText("Data di Nascita");
        TextField txtRegId = new TextField(); txtRegId.setPromptText("Numero Iscrizione Albo");

        Button btnRegister = new Button("Registra Nutrizionista");
        btnRegister.setStyle(GuiTheme.BTN_PRIMARY_STYLE);
        btnRegister.setPrefWidth(200);

        btnRegister.setOnAction(e -> {
            try {
                ProfileBean bean = new ProfileBean(txtName.getText(), txtSurname.getText(), txtEmail.getText(), txtPass.getText(), dateBirth.getValue());
                controller.registerNutritionist(bean , txtRegId.getText());
            } catch (Exception _) {
                showErrorMessage("Compila tutti i campi correttamente.");
            }
        });

        container.getChildren().addAll(
                new Label("Nome:"), txtName,
                new Label("Cognome:"), txtSurname,
                new Label(email2), txtEmail,
                new Label(password2), txtPass,
                new Label("Data Nascita:"), dateBirth,
                new Label("Num. Iscrizione Albo:"), txtRegId,
                new Label(""), // Spaziatore
                btnRegister
        );
    }

    @Override
    public void switchToLogin() {
        showLoginScreen();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.showError(stage, message);
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.showSuccess(stage, message);


        if (message.contains("registrato")) {
            showLoginScreen();
        }
    }

    @Override
    public void close() {
        //Il metodo non fa niente perché la chiusura consiste semplicemente dal set della nuova scena sullo stage da parte della nuova view
    }


}
