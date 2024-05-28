package com.kentwentyfour.project12.presentation.controllers;

import javafx.fxml.FXML;
import com.kentwentyfour.project12.MainApplication;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

/**
 * Controller for the start view.
 */
public class StartController extends BaseController {

    @FXML
    protected void onStartGameClick(){
        this.main.showGameSetupMenu();
    }

    @FXML
    protected void onExitButtonClick() {
        main.exit();
    }

    public void setMainApplication(MainApplication mainApplication) {
        this.main = mainApplication;
    }

}
