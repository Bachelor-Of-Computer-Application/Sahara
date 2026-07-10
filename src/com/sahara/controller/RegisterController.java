package com.sahara.controller;

import com.sahara.service.AuthService;
import com.sahara.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML private TextField passwordVisibleField;
    @FXML private Button togglePasswordBtn;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private Button toggleConfirmPasswordBtn;

    @FXML private Label nameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label ageErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    // ── Runs automatically after the FXML loads ───
    @FXML
    private void initialize() {
        // Only PATIENT and CAREGIVER can self-register; ADMIN is seeded in the DB.
        roleBox.setItems(FXCollections.observableArrayList("PATIENT", "CAREGIVER"));
        roleBox.getSelectionModel().selectFirst();

        genderBox.setItems(FXCollections.observableArrayList("MALE", "FEMALE", "OTHER"));
        genderBox.getSelectionModel().selectFirst();

        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    // ── Eye icons: toggle showing each password in plain text ──
    @FXML
    private void handleTogglePassword(ActionEvent event) {
        passwordVisible = !passwordVisible;
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);
        passwordVisibleField.setVisible(passwordVisible);
        passwordVisibleField.setManaged(passwordVisible);
        togglePasswordBtn.setText(passwordVisible ? "🙈" : "👁");
    }

    @FXML
    private void handleToggleConfirmPassword(ActionEvent event) {
        confirmPasswordVisible = !confirmPasswordVisible;
        confirmPasswordField.setVisible(!confirmPasswordVisible);
        confirmPasswordField.setManaged(!confirmPasswordVisible);
        confirmPasswordVisibleField.setVisible(confirmPasswordVisible);
        confirmPasswordVisibleField.setManaged(confirmPasswordVisible);
        toggleConfirmPasswordBtn.setText(confirmPasswordVisible ? "🙈" : "👁");
    }

    // ── Register button ───────────────────────────
    @FXML
    private void handleRegister(ActionEvent event) {
        // Clear all previous error messages first
        nameErrorLabel.setText("");
        emailErrorLabel.setText("");
        phoneErrorLabel.setText("");
        ageErrorLabel.setText("");
        passwordErrorLabel.setText("");
        confirmPasswordErrorLabel.setText("");
        messageLabel.setText("");

        boolean hasError = false;

        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            nameErrorLabel.setText("Full name is required.");
            hasError = true;
        }

        String email = emailField.getText();
        if (email == null || email.trim().isEmpty()) {
            emailErrorLabel.setText("Email is required.");
            hasError = true;
        } else if (!email.contains("@") || !email.contains(".")) {
            emailErrorLabel.setText("Enter a valid email address.");
            hasError = true;
        }

        String phone = phoneField.getText();
        if (phone == null || phone.trim().isEmpty()) {
            phoneErrorLabel.setText("Phone number is required.");
            hasError = true;
        }

        int age = 0;
        String ageText = ageField.getText();
        if (ageText == null || ageText.trim().isEmpty()) {
            ageErrorLabel.setText("Age is required.");
            hasError = true;
        } else {
            try {
                age = Integer.parseInt(ageText.trim());
                if (age <= 0 || age > 120) {
                    ageErrorLabel.setText("Enter a valid age (1-120).");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                ageErrorLabel.setText("Age must be a number.");
                hasError = true;
            }
        }

        String password = passwordField.getText();
        if (password == null || password.length() < 6) {
            passwordErrorLabel.setText("Must be at least 6 characters.");
            hasError = true;
        }

        String confirmPassword = confirmPasswordField.getText();
        if (password != null && !password.equals(confirmPassword)) {
            confirmPasswordErrorLabel.setText("Passwords do not match.");
            hasError = true;
        }

        if (hasError) return;

        AuthService.Result result = authService.register(
                name,
                email,
                phone,
                password,
                confirmPassword,
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