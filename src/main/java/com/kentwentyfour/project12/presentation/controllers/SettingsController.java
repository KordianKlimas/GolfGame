package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Bots.AdvancedBot;
import com.kentwentyfour.project12.Bots.BotPlayer;
import com.kentwentyfour.project12.Player;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private Label turnIndicatorLabel;

    private String selectedGame;
    private double startX;
    private double startY;
    private double targetX;
    private double targetY;
    private double targetRadius;
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private ArrayList<GolfBall> balls;
    private BotPlayer bot;
    private boolean isMultiplayer = false;
    private List<Player> players;
    private int currentPlayerIndex = 0;
    private Hole hole; // Add this field

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
    }

    @FXML
    public void hit() {
        double xVelocityValue = vx.getValue();
        double yVelocityValue = vy.getValue();
        CoordinatesPath coordinatesPath = physicsEngine.calculateCoordinatePath(balls.get(currentPlayerIndex), xVelocityValue, yVelocityValue);
        mapManager.animateMovableObject(balls.get(currentPlayerIndex), coordinatesPath);
        String stopping = coordinatesPath.getStoppingCondition();
        handleStop(stopping, coordinatesPath.getPath());
    }

    @FXML
    public void BotMove() {
        this.bot = new AdvancedBot();
        CoordinatesPath coordinatesPath = bot.calculatePath(balls.get(currentPlayerIndex));
        mapManager.animateMovableObject(balls.get(currentPlayerIndex), coordinatesPath);
        String stopping = coordinatesPath.getStoppingCondition();
        handleStop(stopping, coordinatesPath.getPath());
    }

    private void handleStop(String condition, double[][] path) {
        Platform.runLater(() -> {
            if(isMultiplayer) {
                if ("outside_of_playable_area".equals(condition) || "obstacle_hit".equals(condition)) {
                    Optional<ButtonType> result = showGameOverPopup(condition);
                    if (result.isPresent()) {
                        if (result.get() == BUTTON_RESTART) {
                            balls.get(currentPlayerIndex).setPosition(startX, startY);
                        } else if (result.get() == BUTTON_CONTINUE) {
                            if (path[0].length > 2) {
                                balls.get(currentPlayerIndex).setPosition(path[0][path[0].length - 2], path[1][path[1].length - 2]);
                            } else {
                                balls.get(currentPlayerIndex).setPosition(path[0][0], path[1][0]);
                            }
                        }
                        mapManager.updateCoordinates(balls.get(currentPlayerIndex));
                        switchTurn();
                    }
                } else if ("ball_in_the_hole".equals(condition)) {
                    Alert1.showWinPopup();
                    switchTurn();
                }
            }else {
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
                        if (path[0].length > 2) {
                            ball.setPosition(path[0][path[0].length - 2], path[1][path[1].length - 2]);
                            //System.err.println(ball.getX()+" "+ball.getY());
                        } else {
                            ball.setPosition(path[0][0], path[1][0]);
                            // System.err.println(ball.getX()+" & "+ball.getY());
                        }
                        mapManager.updateCoordinates(ball);
                    }
                }
            }
        });

    }

    private void switchTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        updateTurnIndicator();
        Platform.runLater(() -> {
            mapManager.updateCoordinates(balls.get(currentPlayerIndex));
        });
    }
    public static Optional<ButtonType> showObstacleHitPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Obstacle hit");
        alert.setHeaderText("The ball hit an obstacle");
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
    }
    private void updateTurnIndicator() {
        turnIndicatorLabel.setText(players.get(currentPlayerIndex).getName() + "'s Turn");
    }

    public static Optional<ButtonType> showGameOverPopup(String condition) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("The ball is " + (condition.equals("outside_of_playable_area") ? "outside of playable area" : "hit an obstacle"));
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
    }

    public void setInitialValues( double startX, double startY, double targetX, double targetY, double targetRadius, MapManager mapManager, PhysicsEngine physicsEngine, ArrayList<GolfBall> balls, Hole hole) {

        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetRadius = targetRadius;
        this.mapManager = mapManager;
        this.physicsEngine = physicsEngine;
        this.balls = balls;
        this.hole = hole; // Save the hole parameter
    }

    public void setGameMode(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
        turnIndicatorLabel.setVisible(isMultiplayer);
    }

    public void setPlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }
        this.players = players;
        currentPlayerIndex = 0; // Reset to the first player
        updateTurnIndicator();
        mapManager.updateCoordinates(balls.get(currentPlayerIndex)); // Show the first player's ball
    }
}
