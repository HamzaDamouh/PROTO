package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.MealPlan;
import edu.ezip.ing1.pds.business.dto.MealPlanItem;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.MealPlanClientService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MealPlanScreen extends VBox {

    private final User user;
    private final MealPlanClientService service;
    private final VBox contentBox = new VBox(20);

    public MealPlanScreen(User user) {
        this.user = user;
        NetworkConfig cfg = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        this.service = new MealPlanClientService(cfg);

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("My Meal Plan");
        header.setFont(Font.font("System", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2e7d32"));

        Button regenerateBtn = new Button("Regenerate");
        regenerateBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        regenerateBtn.setOnAction(e -> loadMealPlan(true));

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> MainUIController.switchToProfile(user));

        HBox btnBar = new HBox(15, regenerateBtn, backBtn);
        btnBar.setAlignment(Pos.CENTER);

        getChildren().addAll(header, btnBar, contentBox);

        loadMealPlan(false);
    }

    private void loadMealPlan(boolean regenerate) {
        contentBox.getChildren().setAll(new ProgressIndicator());

        Task<MealPlan> task = new Task<MealPlan>() {
            @Override
            protected MealPlan call() throws Exception {
                if (regenerate) {
                    service.generateMealPlan(user);
                }
                return service.getMealPlan(user);
            }
        };
        task.setOnSucceeded(evt -> displayPlan(task.getValue()));
        task.setOnFailed(evt -> Platform.runLater(() -> {
            contentBox.getChildren().clear();
            showError("Unable to load plan: " + task.getException().getMessage());
        }));
        new Thread(task).start();
    }

    private void displayPlan(MealPlan plan) {
        contentBox.getChildren().clear();

        // Group items by meal type
        Map<MealTypeEnum, List<MealPlanItem>> byType = new EnumMap<>(MealTypeEnum.class);
        for (MealPlanItem item : plan.getItems()) {
            byType
                    .computeIfAbsent(item.getMealType(), t -> new ArrayList<>())
                    .add(item);
        }

        // For each meal type in a consistent order…
        for (MealTypeEnum type : MealTypeEnum.values()) {
            List<MealPlanItem> items = byType.getOrDefault(type, Collections.emptyList());
            if (items.isEmpty()) continue;  // skip if none

            // Section header
            Label sectionLbl = new Label(type.name().replace('_',' ').toUpperCase());
            sectionLbl.setFont(Font.font("System", FontWeight.BOLD, 16));
            sectionLbl.setTextFill(Color.web("#2e7d32"));

            VBox sectionBox = new VBox(5, sectionLbl);
            sectionBox.setPadding(new Insets(10));
            sectionBox.setStyle("-fx-background-color: #f1f8e9; -fx-background-radius: 8;");

            // List each item and tally calories
            int totalCal = 0;
            for (MealPlanItem it : items) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label mealLbl = new Label(it.getMealName());
                mealLbl.setFont(Font.font("System", FontWeight.NORMAL, 14));

                Label calLbl  = new Label(it.getCalories() + " kcal");
                calLbl.setFont(Font.font("System", FontWeight.NORMAL, 14));

                row.getChildren().addAll(mealLbl, calLbl);
                sectionBox.getChildren().add(row);

                totalCal += it.getCalories();
            }

            // Totals
            Label totLbl = new Label("Total: " + totalCal + " kcal");
            totLbl.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
            totLbl.setTextFill(Color.web("#388e3c"));
            sectionBox.getChildren().add(totLbl);

            contentBox.getChildren().add(sectionBox);
        }

        // If absolutely no items at all, show a friendly message:
        if (contentBox.getChildren().isEmpty()) {
            Label empty = new Label("No meal plan available. Click “Regenerate” to create one.");
            empty.setFont(Font.font("System", FontWeight.NORMAL, 14));
            contentBox.getChildren().add(empty);
        }
    }
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
