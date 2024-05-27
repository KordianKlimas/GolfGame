package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.GameObjects.GolfBall;
import com.kentwentyfour.project12.GameObjects.MapManager;
import com.kentwentyfour.project12.GameObjects.MovableObjects;
import com.kentwentyfour.project12.PhysicsEnginePackage.CoordinatesPath;
import com.kentwentyfour.project12.PhysicsEnginePackage.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Label xVelocity;
    @FXML
    private Label yVelocity;
    @FXML
    private Slider vy;
    @FXML
    private Slider vx;
    @FXML
    private Button hit;
    @FXML
    // all variables
    private String selectedGame;
    private double startX;
    private double startY;
    private double targetX;
    private double targetY;
    private double targetRadius;
    private double staticFrictionSand;
    private double kineticFrictionSand;
    private double staticFrictionGrass;
    private double kineticFrictionGrass;
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private ArrayList<GolfBall> balls;

    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vy.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double y = newValue.doubleValue();
                yVelocity.setText(String.format("Y Velocity: %.2f", y));
            }
        });

        vx.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double x = newValue.doubleValue();
                xVelocity.setText(String.format("X Velocity: %.2f", x));
            }
        });

    }
    @FXML
    public void hit () {
        double xVelocityValue = vx.getValue();
        double yVelocityValue = vy.getValue();
        System.out.println("X Velocity: " + xVelocityValue);
        System.out.println("Y Velocity: " + yVelocityValue);
        //balls.getFirst().setPosition(xVelocityValue,yVelocityValue);
        //mapManager.updateCoordinates(balls.getFirst());
        CoordinatesPath coordinatesPath=  physicsEngine.calculateCoordinatePath(balls.getFirst(),xVelocityValue,yVelocityValue);
        mapManager.animateMovableObject(balls.getFirst(),coordinatesPath);

    }

    public void setInitialValues(String selectedGame, double startX, double startY, double targetX, double targetY, double targetRadius, MapManager mapManager, PhysicsEngine physicsEngine, ArrayList<GolfBall> balls, MovableObjects hole) {
        // Set the selected game mode
        this.selectedGame = selectedGame;

        // Set the initial positions and properties of the objects
        // Set the initial positions and properties of the objects
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetRadius = targetRadius;

        // Set the map manager and physics engine
        this.mapManager = mapManager;
        this.physicsEngine = physicsEngine;

        // Set the list of golf balls
        this.balls = balls;
     }
}

//        vy.setOnMouseReleased((MouseEvent event) -> {
//            numberY = (double) vy.getValue();
//            System.out.println(String.format("Y Velocity: %.2f", numberY));        });
//
//        // Print value when mouse is released on vx slider
//        vx.setOnMouseReleased((MouseEvent event) -> {
//            numberX = (double) vx.getValue();
//            System.out.println(String.format("X Velocity: %.2f", numberX));        });





