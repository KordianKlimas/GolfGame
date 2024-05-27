package com.kentwentyfour.project12.presentation.controllers;

import javafx.scene.control.Alert;

public class Alert1 {
    public static void showWinPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText("You won!");

        alert.showAndWait();
    }
}
