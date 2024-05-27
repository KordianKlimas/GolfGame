package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Constants;
import com.kentwentyfour.project12.GameObjects.GolfBall;
import com.kentwentyfour.project12.GameObjects.Hole;
import com.kentwentyfour.project12.GameObjects.MapManager;
import com.kentwentyfour.project12.PhysicsEnginePackage.CoordinatesPath;
import com.kentwentyfour.project12.PhysicsEnginePackage.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
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

    @FXML
    protected void onStartGameButtonClick() {
        String selectedGame = gameComboBox.getValue();

        double startX = strToDouble(startXField.getText().isEmpty() ? "1" : startXField.getText());
        double startY = strToDouble(startYField.getText().isEmpty() ? "1" : startYField.getText());
        double targetX = strToDouble(targetXField.getText().isEmpty() ? "2" : targetXField.getText());
        double targetY = strToDouble(targetYField.getText().isEmpty() ? "2" : targetYField.getText());
        double ballRadius = strToDouble(ballRadiusField.getText().isEmpty() ? "0.1" : ballRadiusField.getText());
        double targetRadius = strToDouble(targetRadiusField.getText().isEmpty() ? "0.15" : targetRadiusField.getText());
        double staticFrictionSand = strToDouble(staticfrictionsand.getText().isEmpty() ? "0.2" : staticfrictionsand.getText());
        double kineticFrictionSand = strToDouble(kineticfrictionsand.getText().isEmpty() ? "0.1" : kineticfrictionsand.getText());
        double staticFrictionGrass = strToDouble(staticfrictiongrass.getText().isEmpty() ? "0.1" : staticfrictiongrass.getText());
        double kineticFrictionGrass = strToDouble(kineticfrictiongrass.getText().isEmpty() ? "0.05" : kineticfrictiongrass.getText());
        String formula = formulaField.getText().isEmpty() ? "sin( ( x - y ) / 7 ) + 0.5" : formulaField.getText();

       //formula = "0.4 * ( 0.9 -  2.718 ^ ( ( -1 * x ^ 2 + y ^ 2 ) / -8 ) )";
       //formula="1";
        ReferenceStore referenceStore = ReferenceStore.getInstance();
        referenceStore.setCourseProfileFormula(formula);
        mapManager = new MapManager();

        referenceStore.setMapManagerReference(mapManager);

        ArrayList<GolfBall> balls =  new ArrayList<GolfBall>();
       balls.add(new GolfBall(startX,startY,.1,ballRadius));
       physicsEngine = new PhysicsEngine();
        referenceStore.setPhysicsEngine(physicsEngine);


        Stage settingsStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kentwentyfour/project12/views/Settings.fxml"));

        Parent settingsRoot;
        try {
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/com/kentwentyfour/project12/views/Settings.fxml"));
            settingsRoot = settingsLoader.load();
            // Get reference store


            //Set frictions
            referenceStore.setFrictionsAreaType("Grass",kineticFrictionGrass,staticFrictionGrass );
            referenceStore.setFrictionsAreaType("Sand",kineticFrictionSand,staticFrictionSand );


            // Initialize mapManager and map
            mapManager = new MapManager();
            mapManager.generateTerrainData();
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
            settingsStage.show();


            // Set initial position of the ball and update coordinates
            //balls.getFirst().setPosition(-4.8, -4.8);
            //mapManager.updateCoordinates(balls.getFirst());

            // Call setInitialValues for SettingsController
            SettingsController settingsController = settingsLoader.getController();
            settingsController.setInitialValues(selectedGame, startX, startY, targetX, targetY, targetRadius, mapManager, physicsEngine, balls,hole);


        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        //CoordinatesPath coordinatesPath=  physicsEngine.calculateCoordinatePath(balls.getFirst(),2,2);
        //mapManager.animateMovableObject(balls.getFirst(),coordinatesPath);


//        VBox root1 = new VBox();
//        root1.getChildren().addAll(settingsRoot, pane);
//
//       // SettingsController controller = loader.getController();
//        //controller.setInitialValues(selectedGame, startX, startY, targetX, targetY, ballRadius, targetRadius, staticFrictionSand, kineticFrictionSand, staticFrictionGrass, kineticFrictionGrass, formula);
//
//        settingsStage.setTitle("Settings");
////        settingsStage.setWidth(800);
////        settingsStage.setHeight(800);
//        settingsStage.setScene(new Scene(root1));
//        settingsStage.show();


    }


}
