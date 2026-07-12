package com.sahara.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/com/sahara/view/sahara-theme.css").toExternalForm()
            );
            primaryStage.setTitle("Sahara — " + title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Screen not found: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static Stage getStage() {
        return primaryStage;
    }
}