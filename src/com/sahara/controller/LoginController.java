package com.sahara.controller;

import com.sahara.dao.UserDAO;
import com.sahara.model.User;
import com.sahara.service.AuthService;
import com.sahara.util.PasswordUtil;
import com.sahara.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Button togglePasswordBtn;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();

    private boolean passwordVisible = false;

    @FXML
    private void initialize() {
        // Keep the masked field and the plain-text field always in sync,
        // so whichever one is showing always has the correct value.
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    // ── Eye icon: toggle showing the password in plain text ──
    @FXML
    private void handleTogglePassword(ActionEvent event) {
        passwordVisible = !passwordVisible;
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);
        passwordVisibleField.setVisible(passwordVisible);
        passwordVisibleField.setManaged(passwordVisible);
        togglePasswordBtn.setText(passwordVisible ? "🙈" : "👁");
    }

    // ── Log In button ─────────────────────────────
    @FXML
    private void handleLogin(ActionEvent event) {
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        messageLabel.setText("");

        String email    = emailField.getText();
        String password = passwordField.getText();

        boolean hasError = false;
        if (email == null || email.trim().isEmpty()) {
            emailErrorLabel.setText("Email is required.");
            hasError = true;
        }
        if (password == null || password.isEmpty()) {
            passwordErrorLabel.setText("Password is required.");
            hasError = true;
        }
        if (hasError) return;

        AuthService.Result result = authService.login(email, password);

        if (!result.success) {
            messageLabel.setText(result.message);
            return;
        }

        routeByRole(result.role);
    }

    // ── "Register here" link ──────────────────────
    @FXML
    private void goToRegister(ActionEvent event) {
        SceneManager.switchScene("/com/sahara/view/Register.fxml", "Register");
    }

    // ── "Forgot password?" link ───────────────────
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your account email and choose a new password");

        ButtonType resetBtn = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField resetEmailField = new TextField();
        resetEmailField.setPromptText("you@example.com");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("At least 6 characters");

        PasswordField confirmNewPasswordField = new PasswordField();
        confirmNewPasswordField.setPromptText("Re-enter new password");

        Label errorLabel = new Label();
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);
        errorLabel.setWrapText(true);

        grid.add(new Label("Email:"), 0, 0);
        grid.add(resetEmailField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmNewPasswordField, 1, 2);
        grid.add(errorLabel, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Tracks whether the reset actually succeeded (so Cancel doesn't
        // trigger a false "success" message after the dialog closes).
        final boolean[] resetSucceeded = {false};

        // Intercept the OK button so we can validate before closing the dialog
        Button resetButtonNode = (Button) dialog.getDialogPane().lookupButton(resetBtn);
        resetButtonNode.addEventFilter(ActionEvent.ACTION, e -> {
            String resetEmail = resetEmailField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmNewPasswordField.getText();

            if (resetEmail == null || resetEmail.trim().isEmpty()) {
                errorLabel.setText("Please enter your email.");
                e.consume();
                return;
            }
            User user = userDAO.getUserByEmail(resetEmail.trim());
            if (user == null) {
                errorLabel.setText("No account found with that email.");
                e.consume();
                return;
            }
            if (newPassword == null || newPassword.length() < 6) {
                errorLabel.setText("New password must be at least 6 characters.");
                e.consume();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match.");
                e.consume();
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            boolean updated = userDAO.updatePassword(user.getUserId(), newHash);
            if (!updated) {
                errorLabel.setText("Something went wrong. Please try again.");
                e.consume();
                return;
            }
            // Only reaches here on real success — let the dialog close normally
            resetSucceeded[0] = true;
        });

        dialog.showAndWait();

        if (resetSucceeded[0]) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Your password has been reset. Please log in.");
        }
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