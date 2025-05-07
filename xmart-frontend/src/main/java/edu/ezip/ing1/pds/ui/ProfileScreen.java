package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.MealPlanClientService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfileScreen extends VBox {

    public ProfileScreen(User user) {
        // === existing setup ===
        setSpacing(30);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Tableau de Bord");
        header.setFont(Font.font("System", FontWeight.BOLD, 26));
        header.setTextFill(Color.web("#2e7d32"));

        Label calorieTarget = new Label((int) user.calculateDailyCalories() + " kcal");
        calorieTarget.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 48));
        calorieTarget.setTextFill(Color.web("#388e3c"));

        Label calorieSub = new Label("Nombre de calorie par jour");
        calorieSub.setFont(Font.font("System", FontWeight.NORMAL, 14));
        calorieSub.setTextFill(Color.GRAY);

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #f1f8e9; -fx-background-radius: 12; -fx-border-radius: 12;");

        card.getChildren().addAll(
                styledRow("Email:", user.getEmail()),
                styledRow("Age:", String.valueOf(user.getAge())),
                styledRow("Taille:", user.getHeightCm() + " cm"),
                styledRow("Poid:", user.getWeightKg() + " kg"),
                styledRow("Genre:", user.getSex().toString()),
                styledRow("Niveau d'activité:", user.getActivityLevel().toString()),
                styledRow("But:", user.getGoal().toString())
        );

        // === new meal-plan buttons ===
        NetworkConfig netCfg = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        MealPlanClientService mealSvc = new MealPlanClientService(netCfg);

        Button generatePlanBtn = new Button("Generation de Plan Alimentaire");
        generatePlanBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        generatePlanBtn.setOnAction(e -> {
            generatePlanBtn.setDisable(true);
            new Thread(() -> {
                try {
                    mealSvc.generateMealPlan(user);
                    Platform.runLater(() -> {
                        showInfo("Plan Généré!");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Error: " + ex.getMessage()));
                } finally {
                    Platform.runLater(() -> generatePlanBtn.setDisable(false));
                }
            }).start();
        });

        Button viewPlanBtn = new Button("Visualisation du Plan");
        viewPlanBtn.setStyle("-fx-background-color: #81c784; -fx-text-fill: white; -fx-font-weight: bold;");
        viewPlanBtn.setOnAction(e -> MainUIController.switchToMealPlan(user));

        HBox mealBtnBox = new HBox(15, generatePlanBtn, viewPlanBtn);
        mealBtnBox.setAlignment(Pos.CENTER);

        // === existing Log Out button ===
        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> MainUIController.switchToLogin());

        getChildren().addAll(header, calorieTarget, calorieSub, card, mealBtnBox, logoutButton);
    }

    private Label styledRow(String label, String value) {
        Label row = new Label(label + " " + value);
        row.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        row.setTextFill(Color.web("#424242"));
        return row;
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
