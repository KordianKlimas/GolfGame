package com.kentwentyfour.project12.gameobjects;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Grass;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Sand;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.gameobjects.movableobjects.Tree;
import com.kentwentyfour.project12.mathpackage.FormulaCalculator;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.ReferenceStore;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *  Allows managing the state of all objects in the visualised in game
 */
public class MapManager {
    public final int WIDTH = 1000;
    public final int HEIGHT = 1000;
    private final int matrixSize = 100;//101
    private double mapWidth = 10; // in meters ex. 10 means coordinates from -5 to 5
    double scaleFactor = WIDTH / mapWidth; // scalar  to  match [m]  with the pixel size
    private MatrixMapArea[][] terrainData;
    private boolean isTerrainDataInitialized = false;
    FormulaCalculator calcCPF = new FormulaCalculator();
    ArrayList<String> CPF_parsed;
    private Pane root; // the map
    private List<MovableObjects> obstacleList = new ArrayList<>(); // List with all obstacles
    private ReferenceStore referenceStore = ReferenceStore.getInstance();



    public MapManager() {
        String CourseProfileFormula = referenceStore.getCourseProfileFormula();
        this.CPF_parsed = new ArrayList<>(calcCPF.parseString(CourseProfileFormula));
        generateTerrainData();
    }

    public void generateTerrainData() {
        isTerrainDataInitialized = true;
        MatrixMapArea[][] terrainData = new MatrixMapArea[matrixSize][matrixSize];
        for (int row = 0; row < matrixSize; ++row) {
            for (int col = 0; col < matrixSize; ++col) {
                // centers rows and columns and scales the map

                double[] arr = matrixToCoordinates(row,col);
                double x = arr[0];
                double y = arr[1];
                double height = this.computeHeight(x, y);
                try {
                    if (height < 0.0) {
                        terrainData[row][col] = new Water(height);
                    } else if (height < 0.3) {
                        terrainData[row][col] = new Sand(height);
                    } else {
                        terrainData[row][col] = new Grass(height);
                    }
                } catch (EmptyStackException var7) {
                    //System.err.println(STR."Error computing height at row \{row}, col \{col}: \{var7.getMessage()}");
                    terrainData[row][col] = new Grass(height);
                }
            }
        }
        this.terrainData = terrainData;

    }
    /**
     * Generates basic map for the game
     * @return Pane - the map with  MappableObjects
     */
    private void createMap() {

        double cellWidth = (double) (WIDTH / (double) matrixSize);
        double cellHeight = (double) (HEIGHT / (double) matrixSize);
        Pane root = new Pane();
        int newNumRows = matrixSize;
        int newNumCols = matrixSize;

        for (int row = 0; row < newNumRows; ++row) {
            for (int col = 0; col < newNumCols; ++col) {
                MatrixMapArea objectOnMap = terrainData[row * matrixSize / newNumRows][col * matrixSize / newNumCols];
                double x = (double) col * cellWidth;
                double y = (double) row * cellHeight;
                Rectangle cell = new Rectangle(x, y, cellWidth, cellHeight);
                Color color = objectOnMap.getColor();
                cell.setFill(color);
                root.getChildren().add(cell);
            }
        }
        this.root = root;
    }
    /**
     * Creates or returns existing map
     * @return Pane - map
     */
    public Pane getMap(){
        if(this.root!= null){
            return this.root;
        }
        createMap();
        return this.root;
    }
    /**
     * Rerenders and returns whole map
     * @return Pane - map
     */
    public Pane getRerenderedMap(){
        createMap();
        return this.root;
    }
    /**
     * Returns object from map based on it's coordinates
     *
     * @param x
     * @param y
     * @return MappableObject
     */
    public MatrixMapArea accessObject(double x, double y) {
        int[] arr = coordinatesToMatrix(x, y);
        // prevents out of boundary  indexes

        if (!(arr[0] >= this.matrixSize) && !(arr[1] >= this.matrixSize) && !(arr[0] <0) && !(arr[1]<0) && isTerrainDataInitialized) {
            return terrainData[arr[1]][arr[0]];
        } else if (!isTerrainDataInitialized) {
            System.err.println("Map not generated");
        }
        return null;  // the index is out of boundary
    }

    /**
     * Computes the course profile formula
     *
     * @param x
     * @param y
     * @return double
     */
    public double computeHeight(double x, double y) {
        calcCPF.setIndependendValue("x", x);
        calcCPF.setIndependendValue("y", y);
        return calcCPF.calculateRPN(CPF_parsed);
    }

    /**
     * Translates coordinates to indexes of cells in the matrix.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return an array containing the indexes of the cell corresponding to the given coordinates,
     * or {@code null} if the coordinates are outside the matrix bounds
     */
    public int[] coordinatesToMatrix(double x, double y) {
        int[] arr = new int[2];
        int midPoint = (int) (this.matrixSize / 2.0 - 0.5); // index of the middle of matrix. We assume this is (0 [m],0 [m]) in
        double changeLimit = (this.matrixSize - 1) / this.mapWidth; // calculates how much coordinate (x or y) must change to change the cell in matrix
        // calculates  indexes
        arr[0] = midPoint + (int) Math.round(x * changeLimit);
        arr[1] = midPoint - (int) Math.round(y * changeLimit);
        return arr;
    }
    /**
     * Translates indexes of cells in the matrix to coordinates.
     *
     * @param x the x-index of the cell
     * @param y the y-index of the cell
     * @return an array containing the coordinates corresponding to the given cell indexes,
     * or {@code null} if the indexes are outside the matrix bounds
     */
    public double[] matrixToCoordinates(int x, int y) {
        double[] arr = new double[2];
        int midPoint = (int) (this.matrixSize / 2.0 - 0.5); // index of the middle of matrix. We assume it is (0 [m],0 [m])
        double changeLimit = (this.matrixSize - 1) / this.mapWidth; // calculates how much coordinate (x or y) must change to change the cell in matrix

        // Calculate coordinates
        arr[0] = (x - midPoint) / changeLimit;
        arr[1] = (y - midPoint) / -changeLimit;

        return arr;
    }
    /**
     * Translates coordinates to pixel on the map
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return an array containing the pixel coordinates corresponding to the given physical coordinates,
     */
    public int[] coordinatesToPixel(double x, double y) {
        int[] arr = new int[2];

        // We assume midPixelX and midPixelY are  (0 [m],0 [m]) of coordinates
        int midPixelX = (int) (this.WIDTH / 2.0 );//middle pixel of the map x
        int midPixelY = (int) (this.HEIGHT / 2.0 );//middle pixel of the map y

        arr[0] = midPixelX + (int) Math.round(x * scaleFactor);
        arr[1] = midPixelY - (int) Math.round(y * scaleFactor);
        return arr;
    }

    /**
     * Adds MovableObject object to map. Scales it from physic unit [meters] to relative pixel size.
     * @param obj
     * @return Pane1
     */
    public  void addMovableObjectToMap (MovableObjects obj){
        Node visualRepresentation = obj.getVisualRepresentation();
        int[] pixelCoords = coordinatesToPixel(obj.getX(),obj.getY());
        visualRepresentation.setLayoutX(pixelCoords[0]);//-obj.getDistanceFromOrigin()*this.scaleFactor); // to be used if there are any objects drawn that are not circle like
        visualRepresentation.setLayoutY(pixelCoords[1]);//-obj.getDistanceFromOrigin()*this.scaleFactor);
        visualRepresentation.setScaleX(this.scaleFactor);
        visualRepresentation.setScaleY(this.scaleFactor);
        this.root.getChildren().add(visualRepresentation);
    }
    /**
     * Updates MovableObject coordinates
     * @param obj
     */
    public  void updateCoordinates (MovableObjects obj){

        Node visualRepresentation = obj.getVisualRepresentation();
        int[] pixelCoords = coordinatesToPixel(obj.getX(),obj.getY());
        //Translating object so  the origin point matches right pixel
        visualRepresentation.setLayoutX(pixelCoords[0]);//-obj.getDistanceFromOrigin()*this.scaleFactor);
        visualRepresentation.setLayoutY(pixelCoords[1]);//-obj.getDistanceFromOrigin()*this.scaleFactor);
        if (!this.root.getChildren().contains(visualRepresentation)) {
            addMovableObjectToMap(obj);
        }
    }

    /**
     * Animates movable objects on the map based on {@link CoordinatesPath}.
     *
     * @param obj The movable object to animate.
     * @param coordinatesPath The path of coordinates defining the animation.
     */

    public void animateMovableObject(MovableObjects obj, CoordinatesPath coordinatesPath) {
        double[][] path = coordinatesPath.getPath();
        double timeInterval =   2.0 / path[0].length ;// coordinatesPath.getTimeInterval();
        Node visualRepresentation = obj.getVisualRepresentation();

        // Create a Timeline for animation
        Timeline timeline = new Timeline();

        for (int i = 0; i < path[0].length; i++) {
            final double x = path[0][i];
            final double y = path[1][i];
            double mapBorder = mapWidth/2;  //mapWidth = 10 then the map is from -5 to 5 in coordinates
            if(!(x<-mapBorder ||  y<-mapBorder || x>mapBorder || y>mapBorder)) { // checks if coordinates are within map
                KeyFrame keyFrame = new KeyFrame(
                        Duration.seconds(i * timeInterval),
                        event -> {
                            obj.setPositionX(x);
                            obj.setPositionY(y);
                            updateCoordinates(obj);
                        }
                );
                timeline.getKeyFrames().add(keyFrame);
            }
        }
        // Start the animation
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Checks for Collision with 1 MovableObject (obstacle) from obstacleList
     * @param
     * @return
     */
    public MovableObjects checkForCollisionWithObstacle(GolfBall ball,double x_coordinate,double y_coordinate) {
        double ballRadius = ball.getDistanceFromOrigin();

        for (MovableObjects obj2 : this.obstacleList) {
            if(obj2 instanceof Tree){
                double obj2Radius = obj2.getDistanceFromOrigin();
                double obj2X = obj2.getX();
                double obj2Y = obj2.getY();

                // Calculate distance between centers of ball and obj2
                double distance = Math.sqrt(Math.pow(obj2X - x_coordinate, 2) + Math.pow(obj2Y - y_coordinate, 2));

                // Check if circles intersect
                if (distance <= ballRadius + obj2Radius) {
                    // System.err.println("Collision detected between ball and object");
                    return obj2; // Return the object that the ball collided with
                }
            }
        }

        return null; // No collision detected
    }

    /**
     * Adds obstacle to map.
     */
    public void addObstacle(MovableObjects obstacle){
        this.obstacleList.add(obstacle);
        addMovableObjectToMap(obstacle);
    }
    public List<MovableObjects> getObstacles() {
        return obstacleList;
    }

    /**
     * Creates rectangle on map from given obstacle area. Dose not rerender map
     *
     * @param area - area type/obstacle
     * @param coordinateX1 - left top coordinate X
     * @param coordinateY1 - left top coordinate Y
     * @param width -width of rectangle area
     * @param height-height of rectangle area
     */
    public void addArea(MatrixMapArea area, double coordinateX1, double coordinateY1, double width, double height) {
        int[] leftTopCellYX = coordinatesToMatrix(coordinateX1, coordinateY1);
        int[] rightBottomCellYX = coordinatesToMatrix(coordinateX1 + width, coordinateY1 + height);

        int cellX1 = leftTopCellYX[1];
        int cellY1 = leftTopCellYX[0];
        int cellX2 = rightBottomCellYX[1];
        int cellY2 = rightBottomCellYX[0];

        if (cellY1 > cellY2) {
            int temp = cellY1;
            cellY1 = cellY2;
            cellY2 = temp;
        }
        if (cellX1 > cellX2) {
            int temp = cellX1;
            cellX1 = cellX2;
            cellX2 = temp;
        }

        for (int i = cellX1; i <= cellX2; i++) {
            for (int d = cellY1; d <= cellY2; d++) {
                if (d >= 0 && d < terrainData[i].length) {
                    terrainData[i][d] = area;
                }
            }
        }
    }
}