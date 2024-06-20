package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Bots.Algorithms.AStarAlgorithm;
import com.kentwentyfour.project12.Bots.Algorithms.Node;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.ObstacleArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.ReboundingObstacle;
import com.kentwentyfour.project12.gameobjects.movableobjects.Tree;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.presentation.controllers.maps.GameSetupLevels;
import com.kentwentyfour.project12.presentation.controllers.maps.GameSetupVariables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kentwentyfour.project12.Utilities.strToDouble;

public class GameSetupController extends BaseController {

    @FXML
    private ComboBox<String> gameComboBox;
    @FXML
    private ComboBox<String> levelComboBox;

    @FXML
    private TextField startXField;
    @FXML
    private TextField startYField;
    @FXML
    private TextField ballRadiusField;
    @FXML
    private TextField staticfrictionsand;
    @FXML
    private TextField targetRadiusField;
    @FXML
    private TextField targetXField;
    @FXML
    private TextField targetYField;
    @FXML
    private TextField kineticfrictionsand;
    @FXML
    private TextField kineticfrictiongrass;
    @FXML
    private TextField  staticfrictiongrass;
    @FXML
    private TextField formulaField;
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private AStarAlgorithm astarAlgorithm;
    @FXML
    private void initialize() {
        // Initialize ComboBox with difficulty levels
        levelComboBox.getItems().addAll("TestMap_1", "TestMap_2", "TestMap_3","TestMap_4 cool map ^-^","test_obstacleArea");
        gameComboBox.getItems().addAll("Single Player", "Multiplayer");
        GameSetupLevels.initializePredefinedSets();
        // Set default values based on selected difficulty level
        levelComboBox.setOnAction(event -> {
            String selectedLevel = levelComboBox.getValue();
            if (selectedLevel != null) {
                GameSetupVariables variables = GameSetupLevels.getVariablesForLevel(selectedLevel);
                if (variables != null) {
                    startXField.setText(String.valueOf(variables.getStartX()));
                    startYField.setText(String.valueOf(variables.getStartY()));
                    ballRadiusField.setText(String.valueOf(variables.getBallRadius()));
                    staticfrictionsand.setText(String.valueOf(variables.getStaticFrictionSand()));
                    targetRadiusField.setText(String.valueOf(variables.getTargetRadius()));
                    targetXField.setText(String.valueOf(variables.getTargetX()));
                    targetYField.setText(String.valueOf(variables.getTargetY()));
                    kineticfrictionsand.setText(String.valueOf(variables.getKineticFrictionSand()));
                    kineticfrictiongrass.setText(String.valueOf(variables.getKineticFrictionGrass()));
                    staticfrictiongrass.setText(String.valueOf(variables.getStaticFrictionGrass()));
                    formulaField.setText(variables.getFormula());
                }
            }
        });
    }

    @FXML
    protected void onStartGameButtonClick() {
        //gets all entered variables
        String selectedGame = gameComboBox.getValue();
        String selectedLevel = levelComboBox.getValue();
        double startX = strToDouble(startXField.getText().isEmpty() ? "1" : startXField.getText());
        double startY = strToDouble(startYField.getText().isEmpty() ? "1" : startYField.getText());
        double targetX = strToDouble(targetXField.getText().isEmpty() ? "2" : targetXField.getText());
        double targetY = strToDouble(targetYField.getText().isEmpty() ? "2" : targetYField.getText());
        double ballRadius = strToDouble(ballRadiusField.getText().isEmpty() ? "0.05" : ballRadiusField.getText());
        double targetRadius = strToDouble(targetRadiusField.getText().isEmpty() ? "0.15" : targetRadiusField.getText());
        double staticFrictionSand = strToDouble(staticfrictionsand.getText().isEmpty() ? "0.2" : staticfrictionsand.getText());
        double kineticFrictionSand = strToDouble(kineticfrictionsand.getText().isEmpty() ? "0.1" : kineticfrictionsand.getText());
        double staticFrictionGrass = strToDouble(staticfrictiongrass.getText().isEmpty() ? "0.1" : staticfrictiongrass.getText());
        double kineticFrictionGrass = strToDouble(kineticfrictiongrass.getText().isEmpty() ? "0.05" : kineticfrictiongrass.getText());
        String formula = formulaField.getText().isEmpty() ? "sin( ( x - y ) / 7 ) + 0.5 " : formulaField.getText();



        //formula = "0.4 * ( 0.9 -  2.718 ^ ( (  x ^ 2 + y ^ 2 ) / -8 ) )";
        //formula = "( 0.05 * sin( 0.1 * x ) * sin( 0.1 * y ) - 0.02 * cos( 0.5 * x ) * cos( 0.5 * y ) )"
        //formula="1";

        // Get reference store
        ReferenceStore referenceStore = ReferenceStore.getInstance();

        //store given formula
        referenceStore.setCourseProfileFormula(formula);

        //create and store MapManager
        mapManager = new MapManager();
        referenceStore.setMapManagerReference(mapManager);

        //mapManager.addArea(new Water(),0,0,-3,-5);

        //create and store golf balls
        ArrayList<GolfBall> balls =  new ArrayList<GolfBall>();
        balls.add(new GolfBall(startX,startY,.1,ballRadius));

        //create and store PhysicsEngine
        physicsEngine = new PhysicsEngine();
        referenceStore.setPhysicsEngine(physicsEngine);

        //create and store path
        astarAlgorithm = new AStarAlgorithm();
        referenceStore.setAStarAlgorithm(astarAlgorithm);

        Stage settingsStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kentwentyfour/project12/views/Settings.fxml"));

        Parent settingsRoot;
        try {
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/com/kentwentyfour/project12/views/Settings.fxml"));
            settingsRoot = settingsLoader.load();

            //Set frictions
            referenceStore.setFrictionsAreaType("Grass",kineticFrictionGrass,staticFrictionGrass );
            referenceStore.setFrictionsAreaType("Sand",kineticFrictionSand,staticFrictionSand );

            if (selectedLevel != null) {
                GameSetupVariables variables = GameSetupLevels.getVariablesForLevel(selectedLevel);
                if (variables != null) {
                    for(ObstacleArea areaObstacles: variables.getAreaObstacles()){
                        mapManager.addArea(areaObstacles,areaObstacles.getCoordinateX1(),areaObstacles.getCoordinateY1(),areaObstacles.getWidth(),areaObstacles.getHeight());
                    }
                    for(ReboundingObstacle reboundingObstacles: variables.getReboundingObstacles()){
                        mapManager.addObstacle(reboundingObstacles);
                    }
                }
            }
            // Initialize mapManager and map
            Pane pane = mapManager.getMap();

            // Add movable objects to the map
            Hole hole = new Hole(targetX,targetY,targetRadius);
            referenceStore.setHoleReference(hole);
            mapManager.addMovableObjectToMap(hole);
            mapManager.addMovableObjectToMap(balls.getFirst());


            // Create the layout
            HBox root = new HBox();
            VBox settingsBox = new VBox();
            settingsBox.getChildren().add(settingsRoot);
            VBox mapBox = new VBox();
            mapBox.getChildren().add(pane);
            root.getChildren().addAll(settingsBox, mapBox);

            // Show the settings stage
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(root));
            root.setStyle("-fx-background-color: #4a9183;");

            settingsStage.show();


            // Call setInitialValues for SettingsController
            SettingsController settingsController = settingsLoader.getController();
            astarAlgorithm = referenceStore.getAStarAlgorithm();
            int[] startMatrix = mapManager.coordinatesToMatrix(startX, startY);
            int[] targetMatrix = mapManager.coordinatesToMatrix(targetX, targetY);

            int startX1 = startMatrix[0];
            int startY1 = startMatrix[1];
            int targetX1 = targetMatrix[0];
            int targetY1 = targetMatrix[1];

            // decrease range for more midpoints 1=max 100=min
          //  List<Node> aStarPath = astarAlgorithm.findPath(mapManager, startX1, startY1, targetX1, targetY1, 100);
          //  referenceStore.setAStarPath(aStarPath);
          //  System.out.println("A* Path coordinates:");
          //  for (Node node : aStarPath) {
          //      double[] coords = mapManager.matrixToCoordinates(node.matrixX, node.matrixY);
          //      System.out.println("Node: (" + coords[0] + ", " + coords[1] + ")");
          //  }
            settingsController.setInitialValues( startX, startY,  mapManager, physicsEngine, balls,hole);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


}