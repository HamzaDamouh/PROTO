package edu.ezip.ing1.pds.ui;

import edu.ezip.ing1.pds.business.dto.Student;
import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.services.StudentService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainUI extends Application {

    private final TextField firstnameField = new TextField();
    private final TextField lastnameField = new TextField();
    private final TextField groupField = new TextField();
    private final TableView<Student> tableView = new TableView<>();
    private StudentService studentService;

    @Override
    public void start(Stage primaryStage) {
        try {
            NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            studentService = new StudentService(networkConfig);
        } catch (Exception e) {
            showError("Failed to load network config: " + e.getMessage());
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        HBox inputBox = new HBox(10);
        firstnameField.setPromptText("First Name");
        lastnameField.setPromptText("Last Name");
        groupField.setPromptText("Group");
        inputBox.getChildren().addAll(firstnameField, lastnameField, groupField);

        HBox buttonBox = new HBox(10);
        Button insertBtn = new Button("Insert");
        Button selectBtn = new Button("Select All");
        buttonBox.getChildren().addAll(insertBtn, selectBtn);

        insertBtn.setOnAction(e -> handleInsert());
        selectBtn.setOnAction(e -> handleSelect());

        TableColumn<Student, String> colFirst = new TableColumn<>("First Name");
        colFirst.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstname()));
        TableColumn<Student, String> colLast = new TableColumn<>("Last Name");
        colLast.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Student, String> colGroup = new TableColumn<>("Group");
        colGroup.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGroup()));
        tableView.getColumns().addAll(colFirst, colLast, colGroup);

        root.getChildren().addAll(inputBox, buttonBox, tableView);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Manager");
        primaryStage.show();
    }

    private void handleInsert() {
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String group = groupField.getText();

        if (firstname.isEmpty() || lastname.isEmpty() || group.isEmpty()) {
            showError("All fields must be filled.");
            return;
        }

        Student student = new Student();
        student.setFirstname(firstname);
        student.setName(lastname);
        student.setGroup(group);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                studentService.insertStudentDirectly(student);
                return null;
            }

            @Override
            protected void succeeded() {
                showInfo("Student inserted successfully.");
                firstnameField.clear();
                lastnameField.clear();
                groupField.clear();
            }

            @Override
            protected void failed() {
                showError("Insert failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void handleSelect() {
        Task<Students> task = new Task<Students>() {
            @Override
            protected Students call() throws Exception {
                return studentService.selectStudents();
            }

            @Override
            protected void succeeded() {
                tableView.getItems().setAll(getValue().getStudents());
            }

            @Override
            protected void failed() {
                showError("Select failed: " + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void showError(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message).showAndWait());
    }

    private void showInfo(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, message).showAndWait());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
