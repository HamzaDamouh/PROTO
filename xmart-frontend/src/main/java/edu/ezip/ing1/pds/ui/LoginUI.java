package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.*;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.UserClientService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginUI extends Application {

    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField ageField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField weightField = new TextField();
    private final ComboBox<SexEnum> sexBox = new ComboBox<>();
    private final ComboBox<ActivityLevelEnum> activityBox = new ComboBox<>();
    private final ComboBox<GoalEnum> goalBox = new ComboBox<>();

    private final Label calorieLabel = new Label();
    private UserClientService userClientService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        userClientService = new UserClientService(config);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        emailField.setPromptText("Email");
        passwordField.setPromptText("Password");

        ageField.setPromptText("Age");
        heightField.setPromptText("Height (cm)");
        weightField.setPromptText("Weight (kg)");

        sexBox.setItems(FXCollections.observableArrayList(SexEnum.values()));
        activityBox.setItems(FXCollections.observableArrayList(ActivityLevelEnum.values()));
        goalBox.setItems(FXCollections.observableArrayList(GoalEnum.values()));

        Button loginButton = new Button("Login");
        Button createButton = new Button("Create Account");

        HBox buttons = new HBox(10, loginButton, createButton);

        root.getChildren().addAll(
                new Label("Login / Create Account"),
                emailField,
                passwordField,
                new Separator(),
                ageField, heightField, weightField,
                sexBox, activityBox, goalBox,
                buttons,
                new Separator(),
                calorieLabel
        );

        loginButton.setOnAction(e -> login(primaryStage));
        createButton.setOnAction(e -> createAccount(primaryStage));

        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

    private void login(Stage stage) {
        String email = emailField.getText();
        String password = passwordField.getText();

        new Thread(() -> {
            try {
                User user = userClientService.loginUser(email, password);
                Platform.runLater(() -> {
                    stage.close();
                    ProfileUI.show(user);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Login failed: " + ex.getMessage()));
            }
        }).start();
    }

    private void createAccount(Stage stage) {
        try {
            User user = new User();
            user.setEmail(emailField.getText());
            user.setPasswordHash(passwordField.getText());
            user.setAge(Integer.parseInt(ageField.getText()));
            user.setHeightCm(Integer.parseInt(heightField.getText()));
            user.setWeightKg(Integer.parseInt(weightField.getText()));
            user.setSex(sexBox.getValue());
            user.setActivityLevel(activityBox.getValue());
            user.setGoal(goalBox.getValue());

            new Thread(() -> {
                try {
                    User created = userClientService.createUser(user);
                    Platform.runLater(() -> {
                        stage.close();
                        ProfileUI.show(user);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Create failed: " + ex.getMessage()));
                }
            }).start();
        } catch (Exception e) {
            showError("Invalid input: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
