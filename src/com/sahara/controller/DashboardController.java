package com.sahara.controller;

import com.sahara.util.SceneManager;
import com.sahara.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Shared placeholder controller used by all three role dashboards.
 * For now it just greets the logged-in user and handles logout.
 * Each role's real features will be built on top of this later.
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML
    private void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + SessionManager.getName());
        }
        if (roleLabel != null) {
            roleLabel.setText("Signed in as " + SessionManager.getRole());
        }
    }

    // ── Logout button ─────────────────────────────
    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }
}
