package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Bot.Bot;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.presentation.controllers.alerts.Alert1;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.ButtonType;

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
    private Bot bot;

    private static final ButtonType BUTTON_RESTART = new ButtonType("Start from the beginning", ButtonBar.ButtonData.OK_DONE);
    private static final ButtonType BUTTON_CONTINUE = new ButtonType("Continue", ButtonBar.ButtonData.CANCEL_CLOSE);

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
        // bot = new Bot(balls.getFirst(), physicsEngine, mapManager);

    }
    @FXML
    public void hit () {
        double xVelocityValue = vx.getValue();
        double yVelocityValue = vy.getValue();

        //Double[][] botCoordinates = bot.newCoordinates(balls.getFirst(), targetX, targetY);
        //balls.getFirst().setPosition(xVelocityValue,yVelocityValue);
        //mapManager.updateCoordinates(balls.getFirst());
        CoordinatesPath coordinatesPath=  physicsEngine.calculateCoordinatePath(balls.getFirst(),xVelocityValue, yVelocityValue);
        mapManager.animateMovableObject(balls.getFirst(),coordinatesPath);
        String stopping = coordinatesPath.getStoppingCondition();
        handleStop(stopping,coordinatesPath.getPath());

    }
    private void handleStop(String condition, double[][] path) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            if ("outside_of_playable_area".equals(condition)) {
                Platform.runLater(() -> {
                    Optional<ButtonType> result = showGameOverPopup();
                    if (result.isPresent()) {
                        if (result.get() == BUTTON_RESTART) {
                            GolfBall firstBall = balls.get(0);
                            if (firstBall != null) {
                                firstBall.setPosition(startX, startY);
                                mapManager.updateCoordinates(firstBall);
                            }
                        } else if (result.get() == BUTTON_CONTINUE) {
                            GolfBall ball = balls.get(0);
                            ball.setPosition(ball.getX(), ball.getY());
                            mapManager.updateCoordinates(ball);
                        }
                    }
                });
            } else if ("ball_in_the_hole".equals(condition)) {
                Platform.runLater(() -> {
                    Alert1.showWinPopup();
                });
            } else if ("obstacle_hit".equals(condition)) {
                Platform.runLater(() -> {
                    Optional<ButtonType> result = showObstacleHitPopup();
                    if (result.isPresent()) {
                        if (result.get() == BUTTON_RESTART) {
                            GolfBall firstBall = balls.get(0);
                            if (firstBall != null) {
                                firstBall.setPosition(startX, startY);
                                mapManager.updateCoordinates(firstBall);
                            }
                        } else if (result.get() == BUTTON_CONTINUE) {
                            GolfBall ball = balls.get(0);
                            if(path[0].length>2){
                                ball.setPosition(path[0][path[0].length - 2], path[1][path[1].length - 2]);
                            }else{
                                ball.setPosition(ball.getX(),ball.getY());
                            }
                            mapManager.updateCoordinates(ball);
                        }
                    }
                });
            }
        }));
        timeline.play();
    }

    public static Optional<ButtonType> showGameOverPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("The ball is outside of playable area");
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> showObstacleHitPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Obstacle hit");
        alert.setHeaderText("The ball hit an obstacle");
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
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
        this.bot = new Bot(balls.getFirst());
    }
    @FXML
    public void BotMove() {
        Double[][] botCoordinates = bot.newCoordinates(balls.get(0), targetX, targetY);
        CoordinatesPath coordinatesPath = physicsEngine.calculateCoordinatePath(balls.get(0), botCoordinates[0][0], botCoordinates[0][1]);
        mapManager.animateMovableObject(balls.get(0), coordinatesPath);
        String stopping = coordinatesPath.getStoppingCondition();
        handleStop(stopping, coordinatesPath.getPath());
    }
}