package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.SaturationService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SaturationScreen extends VBox {

    private final Button saturateButton = new Button("Saturer la Connexion");
    private final Button backButton = new Button("Retour");

    private final SaturationService saturationService;

    public SaturationScreen() {
        NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
        this.saturationService = new SaturationService(config);

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);

        Label title = new Label("Tester la Saturation du Pool");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        saturateButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
        backButton.setStyle("-fx-background-color: #81c784; -fx-text-fill: white;");

        saturateButton.setOnAction(e -> startSaturation());
        backButton.setOnAction(e -> MainUIController.switchToLogin());

        getChildren().addAll(title, saturateButton, backButton);
    }

    private void startSaturation() {
        saturateButton.setDisable(true);
        new Thread(() -> {
            try {
                String result = saturationService.startSaturation();
                Platform.runLater(() -> {
                    showInfo(result);
                    saturateButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur de communication : " + e.getMessage());
                    saturateButton.setDisable(false);
                });
            }
        }).start();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
