package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainUIController extends Application {

    private static StackPane rootPane;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        rootPane = new StackPane();
        rootPane.setAlignment(Pos.CENTER);

        // initial screen
        rootPane.getChildren().add(new LoginScreen(MainUIController::switchToProfile));

        Scene scene = new Scene(rootPane, 400, 600);
        stage.setScene(scene);
        stage.setTitle("EPISAINE");
        stage.show();
    }

    public static void switchToProfile(User user) {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new ProfileScreen(user));
    }

    public static void switchToLogin() {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new LoginScreen(MainUIController::switchToProfile));
    }

    public static void switchToSignUp() {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new SignUpScreen(MainUIController::switchToProfile));
    }

    public static void switchToMealPlan(User user) {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(new MealPlanScreen(user));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
