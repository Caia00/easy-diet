package view.gui.utility;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GuiTheme {
    public static final String COL_PRIMARY = "#4CAF50";
    public static final String COL_SECONDARY = "#2196F3";
    public static final String COL_DANGER = "#F44336";
    public static final String COL_TEXT_WHITE = "#FFFFFF";
    private static final String STRING = "-fx-background-color: ";

    //Stili riutilizzabili
    public static final String BTN_PRIMARY_STYLE =
            STRING + COL_PRIMARY + "; " +
                    "-fx-text-fill: " + COL_TEXT_WHITE + "; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;";

    public static final String BTN_DANGER_STYLE =
            STRING + COL_DANGER + "; " +
                    "-fx-text-fill: " + COL_TEXT_WHITE + "; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;";

    public static final String BTN_SECONDARY_STYLE =
            STRING + COL_SECONDARY + "; " +
                    "-fx-text-fill: " + COL_TEXT_WHITE + "; " +
                    "-fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 5;";

    private GuiTheme() {
        //Costruttore creato solo per nascondere quello pubblico implicito
    }


    public static HBox createHeader(String title, Runnable onBackOrLogout, boolean isLogout) {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #DDDDDD; -fx-border-width: 0 0 1 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String btnText = isLogout ? "Logout" : "Indietro";
        Button btnAction = new Button(btnText);
        btnAction.setStyle(isLogout ? BTN_DANGER_STYLE : BTN_SECONDARY_STYLE);

        if (onBackOrLogout != null) {
            btnAction.setOnAction(e -> onBackOrLogout.run());
        } else {
            btnAction.setVisible(false);
        }

        header.getChildren().addAll(lblTitle, spacer, btnAction);
        return header;
    }
}
