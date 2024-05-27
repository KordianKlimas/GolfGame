package com.kentwentyfour.project12;

import com.kentwentyfour.project12.presentation.controllers.BaseController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    private Stage stage;
    //test

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Override the start method of the Application class.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        // Set the title of the stage
        stage.setTitle("Putting Game | Group 24");
        // Set the stage properties
        stage.setFullScreen(true);
        // Show the menu
        showStart();
    }

    /**
     * Show a view in the stage
     *
     * @param fxml FXML View File Path
     * @param css  CSS File Path
     */
    private void showView(String fxml, String css) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml));
            Parent root = fxmlLoader.load();

            if (css != null) {
                String cssFile = Objects.requireNonNull(MainApplication.class.getResource(css)).toExternalForm();
                root.getStylesheets().add(cssFile);
            }

            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setFullScreen(true);
                stage.setScene(scene);
            } else {
                stage.setFullScreen(true);
                scene.setRoot(root);
            }

            // Get the controller instance from the FXMLLoader
            BaseController sceneController = fxmlLoader.getController();
            sceneController.setStage(stage);
            sceneController.setMain(this);

            // Show the stage after configuring its properties
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Show the Menu View in the stage
     */
    public void showStart() {
        showView("views/start-view.fxml", "css/menu.css");
    }

    public void showGameSetupMenu() {
        showView("views/game-setup-menu.fxml", "css/setup-menu.css");
    }

    /*
     * Exit the program
     */
    public void exit() {
        stage.close();
    }



}