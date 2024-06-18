package com.kentwentyfour.project12.presentation.controllers;

import com.kentwentyfour.project12.Bots.Algorithms.AStarAlgorithm;
import com.kentwentyfour.project12.Bots.Algorithms.Node;
import com.kentwentyfour.project12.Constants;
<<<<<<< HEAD
import com.kentwentyfour.project12.Player;
=======
>>>>>>> origin/game
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
<<<<<<< HEAD
    private List<Player> players;

    @FXML
    private TextField playerCountInput;


=======
    private AStarAlgorithm astarAlgorithm;
>>>>>>> origin/game

    @FXML
    protected void onStartGameButtonClick() {
        //gets all entered variables
        int playerCount = Integer.parseInt(playerCountInput.getText().trim());
      //  String selectedGame = gameComboBox.getValue();

        boolean isMultiplayer = playerCount>0;

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

        //create and store golf balls
        ArrayList<GolfBall> balls =  new ArrayList<GolfBall>();
        players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            balls.add(new GolfBall(startX,startY,.1,ballRadius)); // Initialize golf ball as needed
            players.add(new Player("Player " +(i+1),balls.get(i))); // Player with ID and associated ball

          //  players.add(player);
        }


       // balls.add(new GolfBall(startX,startY,.1,ballRadius));
        //players = new ArrayList<>();
       // players.add(new Player("Player 1",balls.get(0)));
       // players.add(new Player("Player 2",balls.get(1)));
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
            mapManager = new MapManager();
            mapManager.generateTerrainData();
            Pane pane = mapManager.getMap();

            // Add movable objects to the map
            Hole hole = new Hole(targetX,targetY,targetRadius);
            referenceStore.setHoleReference(hole);
            mapManager.addMovableObjectToMap(hole);
            mapManager.addMovableObjectToMap(balls.getFirst());

<<<<<<< HEAD
=======
            mapManager.addObstacle(new Tree(1,-2,.6));
            mapManager.addObstacle(new Tree(4,1,.7));
            mapManager.addObstacle(new Tree(-2,-4,.5));
            mapManager.addObstacle(new Tree(4,-4,.8));

>>>>>>> origin/game
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
<<<<<<< HEAD
           SettingsController settingsController = settingsLoader.getController();
            settingsController.setInitialValues( startX, startY, targetX, targetY, targetRadius, mapManager, physicsEngine, balls,hole);
         //   int playerCount = playerCountSpinner.getValue(); // Get the number of players
=======
            SettingsController settingsController = settingsLoader.getController();
            astarAlgorithm = referenceStore.getAStarAlgorithm();
            int[] startMatrix = mapManager.coordinatesToMatrix(startX, startY);
            int[] targetMatrix = mapManager.coordinatesToMatrix(targetX, targetY);

            int startX1 = startMatrix[0];
            int startY1 = startMatrix[1];
            int targetX1 = targetMatrix[0];
            int targetY1 = targetMatrix[1];

            // decrease range for more midpoints 1=max 100=min
            List<Node> aStarPath = astarAlgorithm.findPath(mapManager, startX1, startY1, targetX1, targetY1, 100);
            referenceStore.setAStarPath(aStarPath);
            System.out.println("A* Path coordinates:");
            for (Node node : aStarPath) {
                double[] coords = mapManager.matrixToCoordinates(node.matrixX, node.matrixY);
                System.out.println("Node: (" + coords[0] + ", " + coords[1] + ")");
            }
            settingsController.setInitialValues(selectedGame, startX, startY, targetX, targetY, targetRadius, mapManager, physicsEngine, balls,hole);
>>>>>>> origin/game

         //   SettingsController gameController = loader.getController();
            settingsController.setGameMode(isMultiplayer); // Set the game mode
            settingsController.setPlayers(players);
         //   for (int i = 0; i < playerCount; i++) {
          //      GolfBall ball = new GolfBall(startX, startY, ballRadius, mapManager);
            //    Player player = new Player(ball);
            //    players.add(player);
          ///  }

            // Pass players to the game controller
          //  gameController.setPlayers(players);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


}
