package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.UserClientService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class LoginScreen extends VBox {

    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Log In");
    private final Button signupButton = new Button("Sign Up");

    private final Consumer<User> onLoginSuccess;
    private final UserClientService userClientService;

    public LoginScreen(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;

        NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        this.userClientService = new UserClientService(config);

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);

        Label title = new Label("Welcome to HealthTrack");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        emailField.setPromptText("Email");
        passwordField.setPromptText("Password");
        emailField.setMaxWidth(250);
        passwordField.setMaxWidth(250);

        loginButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        signupButton.setStyle("-fx-background-color: #81c784; -fx-text-fill: white; -fx-font-weight: bold;");

        loginButton.setOnAction(e -> login());
        signupButton.setOnAction(e -> MainUIController.switchToSignUp());

        getChildren().addAll(title, emailField, passwordField, loginButton, signupButton);
    }

    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        new Thread(() -> {
            try {
                User user = userClientService.loginUser(email, password);
                Platform.runLater(() -> onLoginSuccess.accept(user));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Login failed: " + e.getMessage()));
            }
        }).start();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
