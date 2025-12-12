package view.gui.utility;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {
    private static final String STYLE_SUCCESS = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;";
    private static final String STYLE_ERROR = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;";

    public static void showSuccess(Stage ownerStage, String message) {
        show(ownerStage, message, true);
    }

    public static void showError(Stage ownerStage, String message) {
        show(ownerStage, message, false);
    }

    private static void show(Stage ownerStage, String message, boolean isSuccess) {
        Popup popup = new Popup();
        Label label = new Label(message);
        label.setStyle(isSuccess ? STYLE_SUCCESS : STYLE_ERROR);
        label.setPadding(new Insets(15, 20, 15, 20)); // Spaziatura interna
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        label.setEffect(new javafx.scene.effect.DropShadow(5, Color.GRAY));

        popup.getContent().add(label);
        popup.show(ownerStage);
        double x = ownerStage.getX() + ownerStage.getWidth() - label.getWidth() - 20;
        double y = ownerStage.getY() + ownerStage.getHeight() - label.getHeight() - 20;

        popup.setX(x);
        popup.setY(y);

        //Fade out dopo qualche secondo
        Timeline timeline = new Timeline();

        KeyFrame keyVisible = new KeyFrame(Duration.millis(2500), new KeyValue(popup.opacityProperty(), 1.0));
        KeyFrame keyFade = new KeyFrame(Duration.millis(3500), new KeyValue(popup.opacityProperty(), 0.0));

        timeline.getKeyFrames().addAll(keyVisible, keyFade);
        timeline.setOnFinished((ae) -> popup.hide());

        timeline.play();
    }
}
