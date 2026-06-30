package com.sahara.controller;

import com.sahara.service.AuthService;
import com.sahara.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private ComboBox<String> roleBox;
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField ageField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    // ── Runs automatically after the FXML loads ───
    @FXML
    private void initialize() {
        // Only PATIENT and CAREGIVER can self-register; ADMIN is seeded in the DB.
        roleBox.setItems(FXCollections.observableArrayList("PATIENT", "CAREGIVER"));
        roleBox.getSelectionModel().selectFirst();

        genderBox.setItems(FXCollections.observableArrayList("MALE", "FEMALE", "OTHER"));
        genderBox.getSelectionModel().selectFirst();
    }

    // ── Register button ───────────────────────────
    @FXML
    private void handleRegister(ActionEvent event) {
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException e) {
            messageLabel.setText("Age must be a number.");
            return;
        }

        AuthService.Result result = authService.register(
                nameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                roleBox.getValue(),
                genderBox.getValue(),
                age,
                addressField.getText()
        );

        if (result.success) {
            // green confirmation, then send them to login
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(result.message);
            SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(result.message);
        }
    }

    // ── "Log in" link ─────────────────────────────
    @FXML
    private void goToLogin(ActionEvent event) {
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }
}
