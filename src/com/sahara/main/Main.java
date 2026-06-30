package com.sahara.main;

import com.sahara.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.switchScene("/com/sahara/view/Login.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}