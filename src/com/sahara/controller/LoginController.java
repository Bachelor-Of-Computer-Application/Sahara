package com.sahara.controller;

import com.sahara.service.AuthService;
import com.sahara.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    // ── Log In button ─────────────────────────────
    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = emailField.getText();
        String password = passwordField.getText();

        AuthService.Result result = authService.login(email, password);

        if (!result.success) {
            messageLabel.setText(result.message);
            return;
        }

        // route to the right dashboard based on role
        routeByRole(result.role);
    }

    // ── "Register here" link ──────────────────────
    @FXML
    private void goToRegister(ActionEvent event) {
        SceneManager.switchScene("/com/sahara/view/Register.fxml", "Register");
    }

    // ── Send the user to their role's home screen ─
    private void routeByRole(String role) {
        switch (role) {
            case "PATIENT":
                SceneManager.switchScene("/com/sahara/view/PatientDashboard.fxml", "Patient Dashboard");
                break;
            case "CAREGIVER":
                SceneManager.switchScene("/com/sahara/view/CaregiverDashboard.fxml", "Caregiver Dashboard");
                break;
            case "ADMIN":
                SceneManager.switchScene("/com/sahara/view/AdminDashboard.fxml", "Admin Dashboard");
                break;
            default:
                messageLabel.setText("Unknown role: " + role);
        }
    }
}
