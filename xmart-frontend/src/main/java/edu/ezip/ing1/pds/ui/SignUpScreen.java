package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.*;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.UserClientService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class SignUpScreen extends VBox {

    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField ageField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField weightField = new TextField();
    private final ComboBox<SexEnum> sexBox = new ComboBox<>();
    private final ComboBox<ActivityLevelEnum> activityBox = new ComboBox<>();
    private final ComboBox<GoalEnum> goalBox = new ComboBox<>();

    private final Button submitButton = new Button("Create Account");
    private final Button backButton = new Button("Back to Login");

    private final Consumer<User> onSuccess;
    private final UserClientService userClientService;

    public SignUpScreen(Consumer<User> onSuccess) {
        this.onSuccess = onSuccess;

        NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        this.userClientService = new UserClientService(config);

        setSpacing(10);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);

        Label title = new Label("Create Your Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        emailField.setPromptText("Email");
        passwordField.setPromptText("Password");
        ageField.setPromptText("Age");
        heightField.setPromptText("Height (cm)");
        weightField.setPromptText("Weight (kg)");

        sexBox.setItems(FXCollections.observableArrayList(SexEnum.values()));
        sexBox.setPromptText("Sex");
        activityBox.setItems(FXCollections.observableArrayList(ActivityLevelEnum.values()));
        activityBox.setPromptText("Activity Level");
        goalBox.setItems(FXCollections.observableArrayList(GoalEnum.values()));
        goalBox.setPromptText("Goal");

        submitButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2e7d32;");

        submitButton.setOnAction(e -> create());
        backButton.setOnAction(e -> MainUIController.switchToLogin());

        getChildren().addAll(
                title, emailField, passwordField, ageField, heightField, weightField,
                sexBox, activityBox, goalBox, submitButton, backButton
        );
    }

    private void create() {
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
                    User result = userClientService.createUser(user);
                    Platform.runLater(() -> onSuccess.accept(result));
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Sign-up failed: " + ex.getMessage()));
                }
            }).start();
        } catch (Exception e) {
            showError("Invalid input: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
