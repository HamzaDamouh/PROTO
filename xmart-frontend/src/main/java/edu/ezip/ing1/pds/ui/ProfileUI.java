package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileUI {

    public static void show(User user) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label header = new Label("Welcome, " + user.getEmail());
        Label calories = new Label("Your daily calorie target: " + (int) user.calculateDailyCalories() + " kcal");

        Label info = new Label("Profile Info:\n"
                + "Age: " + user.getAge() + "\n"
                + "Sex: " + user.getSex() + "\n"
                + "Height: " + user.getHeightCm() + " cm\n"
                + "Weight: " + user.getWeightKg() + " kg\n"
                + "Activity Level: " + user.getActivityLevel() + "\n"
                + "Goal: " + user.getGoal());

        root.getChildren().addAll(header, calories, info);

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("User Profile");
        stage.show();
    }
}
