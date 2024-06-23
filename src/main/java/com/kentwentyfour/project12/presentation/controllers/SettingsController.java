package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.bots.BotHillClimbing;
import com.kentwentyfour.project12.bots.BasicBot;
import com.kentwentyfour.project12.bots.BotPlayer;
import com.kentwentyfour.project12.bots.MultipleTurnBot;
import com.kentwentyfour.project12.bots.improvedbot.MazeBot;
import com.kentwentyfour.project12.bots.improvedbot.resources.Waypoint;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ButtonType;

public class SettingsController implements Initializable {
    @FXML
    public ComboBox botChooseBox;

    @FXML
    public Label turnCounter;

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
    // all variables
    private  String selectedLevel;
    private int turnCount=1;
    private int  multipleBotTurnsCounter = 0;

    boolean multipleBotTurns = false;
    private double startX;
    private double startY;
    private double targetX;
    private double targetY;
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private ArrayList<GolfBall> balls;
    private BotPlayer bot;
    private  final ButtonType BUTTON_RESTART = new ButtonType("Start from the beginning", ButtonBar.ButtonData.OK_DONE);
    private  final ButtonType BUTTON_CONTINUE = new ButtonType("Continue", ButtonBar.ButtonData.CANCEL_CLOSE);
    private  final ButtonType BUTTON_END_GAME = new ButtonType("End Game", ButtonBar.ButtonData.CANCEL_CLOSE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        botChooseBox.getItems().addAll("Basic Bot","HillClimbing Bot","Maze bot" );
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
        CoordinatesPath coordinatesPath=  physicsEngine.calculateCoordinatePath(balls.getFirst(),xVelocityValue, yVelocityValue);
        mapManager.animateMovableObject(balls.getFirst(),coordinatesPath);
        String stopping = coordinatesPath.getStoppingCondition();
        handleStop(stopping,coordinatesPath.getPath());
        turnCounter.setText("Turn: "+turnCount++);

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

                                turnCount=1;
                                turnCounter.setText("Turn: "+turnCount);
                                multipleBotTurnsCounter = 0;
                                multipleBotTurns =false;
                            }
                        } else if (result.get() == BUTTON_CONTINUE) {
                            GolfBall ball = balls.get(0);
                            ball.setPosition(ball.getX(), ball.getY());

                            mapManager.updateCoordinates(ball);
                            if(multipleBotTurns){
                                multipleBotTurnsCounter++;
                                BotMoveMultipleTurns(multipleBotTurnsCounter);
                            }
                        }
                    }
                });
            } else if ("ball_in_the_hole".equals(condition)) {
                multipleBotTurns =false;
                Platform.runLater(() -> {
                    Optional<ButtonType> result = showWinPopup();
                    if (result.isPresent()) {
                        if (result.get() == BUTTON_RESTART) {
                            GolfBall firstBall = balls.get(0);
                            if (firstBall != null) {
                                firstBall.setPosition(startX, startY);
                                mapManager.updateCoordinates(firstBall);
                                turnCount=1;
                                turnCounter.setText("Turn: "+turnCount);
                                multipleBotTurnsCounter = 0;
                            }
                        } else if (result.get() == BUTTON_END_GAME) {
                            Platform.runLater(() -> {
                                Stage stage = (Stage) vx.getScene().getWindow();
                                stage.close();
                            });
                        }
                    }
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
                                turnCount=1;
                                turnCounter.setText("Turn: "+turnCount);
                            }
                            multipleBotTurnsCounter = 0;
                            multipleBotTurns =false;
                        } else if (result.get() == BUTTON_CONTINUE) {
                            GolfBall ball = balls.get(0);
                            //sets ball position to last valid coordinates
                            if(path[0].length>2){
                                ball.setPosition(path[0][path[0].length - 2], path[1][path[1].length - 2]);
                            }else{
                                ball.setPosition(path[0][0], path[1][0]);
                            }
                            mapManager.updateCoordinates(ball);
                            if(multipleBotTurns){
                                multipleBotTurnsCounter++;
                                BotMoveMultipleTurns(multipleBotTurnsCounter);
                            }
                        }
                    }
                });
            }else  if(multipleBotTurns){
                multipleBotTurnsCounter++;
                BotMoveMultipleTurns(multipleBotTurnsCounter);
            }
        }));
        timeline.play();
    }
    private Optional<ButtonType> showGameOverPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("The ball is outside of playable area");
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
    }

    private Optional<ButtonType> showObstacleHitPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Obstacle hit");
        alert.setHeaderText("The ball hit an obstacle");
        alert.setContentText("Choose action:");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_CONTINUE);
        return alert.showAndWait();
    }
    private Optional<ButtonType> showWinPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText("You won!");
        alert.getButtonTypes().setAll(BUTTON_RESTART, BUTTON_END_GAME);
        return alert.showAndWait();
    }

    public void setInitialValues( double startX, double startY,  MapManager mapManager, PhysicsEngine physicsEngine, ArrayList<GolfBall> balls,double targetX,double targetY, String selectedLevel) {
        // Set the selected game mode
        // Set the initial positions and properties of the objects
        this.startX = startX;
        this.startY = startY;

        this.targetX = targetX;
        this.targetY = targetY;

        this.selectedLevel = selectedLevel;

        // Set the map manager and physics engine
        this.mapManager = mapManager;
        this.physicsEngine = physicsEngine;

        // Set the list of golf balls
        this.balls = balls;
    }
    public void  BotMoveMultipleTurns(int multipleBotTurnsCounter){
        MultipleTurnBot botMT = (MultipleTurnBot) bot;

        if(multipleBotTurnsCounter+1<botMT.getCurrentWaypointPath().size()) {
            double waypointCoordX = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter).x;
            double waypointCoordY = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter).y;

            double nextWaypointCoordX = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter + 1).x;
            double nextWaypointCoordY = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter + 1).y;

            GolfBall ball = balls.getFirst();
            double ballCoordX = ball.getX();
            double ballCoordY = ball.getY();

            // checks if the ball landed at waypoint
            if (!(waypointCoordX == ballCoordX && waypointCoordY == ballCoordY)) {
                botMT.genereteWaypointPath(targetX, targetY);
                nextWaypointCoordX = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter).x;
                nextWaypointCoordY = botMT.getCurrentWaypointPath().get(multipleBotTurnsCounter).y;
            }
            CoordinatesPath coordinatesPath = botMT.calculatePath(ball, nextWaypointCoordX, nextWaypointCoordY);
            mapManager.animateMovableObject(balls.getFirst(), coordinatesPath);
            String stopping = coordinatesPath.getStoppingCondition();
            multipleBotTurns = true;
            handleStop(stopping, coordinatesPath.getPath());
        }
        else{
            System.err.println("the full path has not been generated");
        }
    }
    public void BotMove() {
        String selectedBot = (String) botChooseBox.getSelectionModel().getSelectedItem();

        try {
            if (selectedBot == null) {
                return;
            }

            boolean multipleTurns =false;
            switch (selectedBot) {
                case "Basic Bot" -> bot = new BasicBot();
                case "Maze bot" -> {
                    bot = new MazeBot();
                    multipleTurns = true;
                }
                case "HillClimbing" -> bot = new BotHillClimbing();
                default -> bot = new BotHillClimbing();
            }
            if(multipleTurns){
                MultipleTurnBot botMT = (MultipleTurnBot) bot;
                botMT.genereteWaypointPath(targetX,targetY);
                CoordinatesPath coordinatesPath = botMT.calculatePath(balls.getFirst(),botMT.getCurrentWaypointPath().getFirst().x, botMT.getCurrentWaypointPath().getFirst().y);
                mapManager.animateMovableObject(balls.getFirst(), coordinatesPath);
                String stopping = coordinatesPath.getStoppingCondition();
                //exportExperimentData(bot,coordinatesPath,selectedLevel);
                multipleBotTurns = true;
                handleStop(stopping, coordinatesPath.getPath());

            }else{
                CoordinatesPath coordinatesPath = bot.calculatePath(balls.getFirst(),targetX, targetY);
                mapManager.animateMovableObject(balls.getFirst(), coordinatesPath);
                String stopping = coordinatesPath.getStoppingCondition();
                exportExperimentData(bot,coordinatesPath,selectedLevel);
                multipleBotTurns = false;
                handleStop(stopping, coordinatesPath.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportExperimentData(BotPlayer bot, CoordinatesPath path,String selectedLevel){
        String csvFilePath = "src/main/java/com/kentwentyfour/project12/_ExperimentsData/experiment_data.csv";

        try (FileWriter writer = new FileWriter(csvFilePath, true)) {
            // If the file doesn't exist yet, write the header
            if (!new File(csvFilePath).exists()) {
                writer.append("Level,BotName,ComputationTime(ms),NumberOfTurns\n");
            }

            double computationTimeMillis = bot.getComputationTime() / 1000000.0; // Convert nanoseconds to milliseconds
            writer.append(String.format("%s,%s,%f,%d\n", selectedLevel, bot.getName(), computationTimeMillis, bot.getNumberOfTurns()));

            System.out.println("Data has been appended to " + csvFilePath);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}