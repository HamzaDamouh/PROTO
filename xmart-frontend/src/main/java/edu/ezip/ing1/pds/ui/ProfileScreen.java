package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfileScreen extends VBox {

    public ProfileScreen(User user) {
        setSpacing(30);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Your Dashboard");
        header.setFont(Font.font("System", FontWeight.BOLD, 26));
        header.setTextFill(Color.web("#2e7d32"));

        Label calorieTarget = new Label((int) user.calculateDailyCalories() + " kcal");
        calorieTarget.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 48));
        calorieTarget.setTextFill(Color.web("#388e3c"));

        Label calorieSub = new Label("Target Calories to Maintain Health");
        calorieSub.setFont(Font.font("System", FontWeight.NORMAL, 14));
        calorieSub.setTextFill(Color.GRAY);

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #f1f8e9; -fx-background-radius: 12; -fx-border-radius: 12;");

        Label email = styledRow("Email:", user.getEmail());
        Label age = styledRow("Age:", String.valueOf(user.getAge()));
        Label height = styledRow("Height:", user.getHeightCm() + " cm");
        Label weight = styledRow("Weight:", user.getWeightKg() + " kg");
        Label sex = styledRow("Sex:", user.getSex().toString());
        Label activity = styledRow("Activity Level:", user.getActivityLevel().toString());
        Label goal = styledRow("Goal:", user.getGoal().toString());

        card.getChildren().addAll(email, age, height, weight, sex, activity, goal);

        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> MainUIController.switchToLogin());

        getChildren().addAll(header, calorieTarget, calorieSub, card, logoutButton);
    }

    private Label styledRow(String label, String value) {
        Label row = new Label(label + " " + value);
        row.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        row.setTextFill(Color.web("#424242"));
        return row;
    }
}
