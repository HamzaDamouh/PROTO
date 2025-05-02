package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.MealPlan;
import edu.ezip.ing1.pds.business.dto.MealPlanItem;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.MealPlanClientService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class MealPlanScreen extends VBox {

    // Inner model: one row per weekday
    private static class DayRow {
        public String dayName;
        public String breakfast;
        public String lunchDinner;
        public String snack;
        public int totalCalories;
        public DayRow(String dayName) { this.dayName = dayName; }
    }

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

        Task<MealPlan> task = new Task<>() {
            @Override
            protected MealPlan call() throws Exception {
                if (regenerate) service.generateMealPlan(user);
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
        List<MealPlanItem> items = plan.getItems();
        int typesPerDay = MealTypeEnum.values().length;
        int days = items.size() / typesPerDay;
        List<DayRow> rows = new ArrayList<>(days);

        int idx = 0;
        for (int d = 0; d < days; d++) {
            LocalDate date = LocalDate.now().plusDays(d);
            String name = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault());
            DayRow row = new DayRow(name);
            row.totalCalories = 0;

            for (int m = 0; m < typesPerDay; m++) {
                MealPlanItem it = items.get(idx++);
                String desc = it.getMealName() + " (" + it.getCalories() + " kcal)";
                switch (it.getMealType()) {
                    case breakfast    -> {
                        row.breakfast = desc;
                        row.totalCalories += it.getCalories();
                    }
                    case lunch_dinner -> {
                        row.lunchDinner = desc;
                        // double lunch/dinner calories
                        row.totalCalories += it.getCalories() * 2;
                    }
                    case snack        -> {
                        row.snack = desc;
                        row.totalCalories += it.getCalories();
                    }
                }
            }
            rows.add(row);
        }

        TableView<DayRow> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(rows));

        TableColumn<DayRow, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().dayName));

        TableColumn<DayRow, String> bCol = new TableColumn<>("Breakfast");
        bCol.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().breakfast));

        TableColumn<DayRow, String> lCol = new TableColumn<>("Lunch/Dinner");
        lCol.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().lunchDinner));

        TableColumn<DayRow, String> sCol = new TableColumn<>("Snack");
        sCol.setCellValueFactory(cd ->
                new ReadOnlyStringWrapper(cd.getValue().snack));

        TableColumn<DayRow, Integer> tCol = new TableColumn<>("Total kcal");
        tCol.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().totalCalories));

        table.getColumns().setAll(dayCol, bCol, lCol, sCol, tCol);
        contentBox.getChildren().setAll(table);
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
