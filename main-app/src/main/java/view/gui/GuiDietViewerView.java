package view.gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.DietItem;
import models.DietPlan;
import models.Meal;
import view.DietViewerView;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import view.gui.utility.GuiTheme;
import java.util.List;

public class GuiDietViewerView implements DietViewerView {
    private static final String[] ORDERED_DAYS = {
            "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"
    };
    private Stage stage;

    @Override
    public void showDietPlan(DietPlan plan) {
        if (stage == null) {
            stage = new Stage();
            stage.setTitle("Anteprima Piano Alimentare");
        }

        BorderPane root = new BorderPane();
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: " + GuiTheme.COL_PRIMARY + ";");
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblTitle = new Label("Piano: " + plan.getDietName());
        lblTitle.setTextFill(javafx.scene.paint.Color.WHITE);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        header.getChildren().add(lblTitle);
        root.setTop(header);

        Accordion accordion = new Accordion();

        for (String day : ORDERED_DAYS) {
            List<Meal> meals = plan.getMealsForDay(day);
            VBox dayContent = new VBox(10);
            dayContent.setPadding(new Insets(10));

            if (meals.isEmpty()) {
                Label emptyLbl = new Label("Nessun pasto previsto.");
                emptyLbl.setStyle("-fx-font-style: italic; -fx-text-fill: #888;");
                dayContent.getChildren().add(emptyLbl);
            } else {
                for (Meal meal : meals) {
                    dayContent.getChildren().add(createMealCard(meal));
                }
            }

            TitledPane dayPane = new TitledPane(day, dayContent);
            dayPane.setAnimated(true);
            accordion.getPanes().add(dayPane);
        }

        if (!accordion.getPanes().isEmpty()) {
            accordion.setExpandedPane(accordion.getPanes().getFirst());
        }

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button btnClose = new Button("Nascondi Anteprima");
        btnClose.setStyle(GuiTheme.BTN_SECONDARY_STYLE);
        btnClose.setOnAction(e -> stage.close());

        bottom.getChildren().add(btnClose);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 450, 700);
        stage.setScene(scene);

        stage.show();
        stage.toFront();
    }


    private VBox createMealCard(Meal meal) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: #DDD; -fx-border-radius: 8; -fx-padding: 10; -fx-background-color: #F9F9F9;");

        HBox mealHeader = new HBox();

        String timeStr = (meal.getTime() != null) ? meal.getTime().toString() : "--:--";
        Label lblName = new Label(meal.getName().toUpperCase() + " (" + timeStr + ")");
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblName.setTextFill(javafx.scene.paint.Color.web(GuiTheme.COL_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblTotalKcal = new Label(String.format("Tot: %.0f Kcal", meal.getTotalKcalTarget()));
        lblTotalKcal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #555;");

        mealHeader.getChildren().addAll(lblName, spacer, lblTotalKcal);
        card.getChildren().add(mealHeader);
        card.getChildren().add(new Separator());

        if (meal.getFoods().isEmpty()) {
            Label empty = new Label("Nessun alimento inserito.");
            empty.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
            card.getChildren().add(empty);
        } else {
            int index = 0;
            for (DietItem item : meal.getFoods()) {

                HBox itemRow = new HBox(10);
                itemRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                itemRow.setPadding(new Insets(3, 0, 3, 0));

                Label lblIndex = new Label("[" + index + "]");
                lblIndex.setStyle("-fx-text-fill: #999; -fx-font-size: 10px;");

                Label lblCategory = new Label(item.getCategory().toString());
                lblCategory.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

                String suggestionText = "";
                if (item.getSuggestedProduct().isPresent()) {
                    suggestionText = " (" + item.getSuggestedProduct().get().getName() + ")";
                }
                Label lblSuggestion = new Label(suggestionText);
                lblSuggestion.setStyle("-fx-font-style: italic; -fx-text-fill: #0288D1;");

                Region rowSpacer = new Region();
                HBox.setHgrow(rowSpacer, Priority.ALWAYS);
                double kcal = item.getTarget().getTargetKcal();
                Label lblKcal = new Label(String.format("%.0f kcal", kcal));
                lblKcal.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

                itemRow.getChildren().addAll(lblIndex, lblCategory, lblSuggestion, rowSpacer, lblKcal);
                card.getChildren().add(itemRow);
                index++;
            }
        }

        return card;
    }


}
