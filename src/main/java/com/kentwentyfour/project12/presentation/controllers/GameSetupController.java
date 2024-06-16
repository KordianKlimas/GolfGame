package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Bots.Algorithms.AStarAlgorithm;
import com.kentwentyfour.project12.Constants;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.Tree;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
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

import static com.kentwentyfour.project12.Utilities.strToDouble;

public class GameSetupController extends BaseController {

    @FXML
    private ComboBox<String> gameComboBox;
    @FXML
    private void initialize() {
        // Initialize ComboBox with options
        gameComboBox.getItems().addAll("Single Player", "Multiplayer");
    }

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
    private GolfBall ball;
    private Constants constants;
    private PhysicsEngine physicsEngine;
    private AStarAlgorithm astarAlgorithm;

    @FXML
    protected void onStartGameButtonClick() {
        //gets all entered variables
        String selectedGame = gameComboBox.getValue();
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

            // Initialize mapManager and map
            Pane pane = mapManager.getMap();

            // Add movable objects to the map
            Hole hole = new Hole(targetX,targetY,targetRadius);
            referenceStore.setHoleReference(hole);
            mapManager.addMovableObjectToMap(hole);
            mapManager.addMovableObjectToMap(balls.getFirst());

            mapManager.addObstacle(new Tree(1,-2,.6));
            mapManager.addObstacle(new Tree(4,1,.7));
            mapManager.addObstacle(new Tree(-2,-4,.5));
            mapManager.addObstacle(new Tree(4,-4,.8));

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
            settingsStage.show();


            // Call setInitialValues for SettingsController
            SettingsController settingsController = settingsLoader.getController();
            settingsController.setInitialValues(selectedGame, startX, startY, targetX, targetY, targetRadius, mapManager, physicsEngine, balls,hole);


        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


}
