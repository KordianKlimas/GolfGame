package com.kentwentyfour.project12.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController extends BaseController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        main.showHello();
    }

    @FXML
    protected void onSinglePlayerClick() {
        onHelloButtonClick();
    }

   @FXML
   protected void onMultiplayerClick() {
       welcomeText.setText("Welcome to JavaFX Application!");
   }

    @FXML
    protected void onBotPlayerClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onExitButtonClick() {
        main.exit();
    }
}