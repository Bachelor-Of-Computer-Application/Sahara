package com.sahara.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    // ── Set the main window ───────────────────────
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    // ── Switch to a different screen ─────────────
    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );
            Parent root = loader.load();
            primaryStage.setTitle("Sahara — " + title);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Screen not found: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ── Get the current stage ─────────────────────
    public static Stage getStage() {
        return primaryStage;
    }
}