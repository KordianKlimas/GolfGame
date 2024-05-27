package com.kentwentyfour.project12.gameobjects;
import java.util.ArrayList;
import java.util.EmptyStackException;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.AreaType;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Grass;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Sand;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areaobstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
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
public class MapManager {
    public final int WIDTH = 600;
    public final int HEIGHT = 600;
    private final int matrixSize = 101;//101
    private double mapWidth = 10; // in meters ex. 10 means coordinates from -5 to 5
    double scaleFactor = WIDTH / mapWidth; // scalar  to  match [m]  with the pixel size
    private MatrixMapArea[][] terrainData;
    private boolean isTerrainDataInitialized = false;
    FormulaCalculator calcCPF = new FormulaCalculator();
    ArrayList<String> CPF_parsed;
    private Pane root; // the map
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
                //double height = this.computeHeight(row/(matrixSize/mapWidth),col/(matrixSize/mapWidth));
                // centers rows and columns and scales the map
               // double x = (row - matrixSize / 2) / (matrixSize / mapWidth);
               // double y = (col - matrixSize / 2) / (matrixSize / mapWidth);
                double[] arr = matrixToCoordinates(row,col);
                double x = arr[0];
                double y = arr[1];
                //System.out.println("x: "+x+"y: "+y);
                double height = this.computeHeight(x, y);
                try {
                    if (height < 0.0) {
                        terrainData[row][col] = new Water();
                    } else if (height < 0.3) {
                        terrainData[row][col] = new Sand();
                    } else {
                        terrainData[row][col] = new Grass(height);
                    }
                } catch (EmptyStackException var7) {
                    System.err.println("Error computing height at row " + row + ", col " + col + ": " + var7.getMessage());
                    terrainData[row][col] = new Grass(height);
                }
                MatrixMapArea area =terrainData[row][col];
                //System.err.println("x: "+x+"y: "+y+" H: "+height);
               // System.err.println("AreaType: "+"Grass: "+(area instanceof Grass)+"Water: "+(area instanceof Water)+"Sand: "+(area instanceof Sand));
            }
        }
        //System.err.println(terrainData[0][0] instanceof Water);

        this.terrainData = terrainData;
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
            return terrainData[arr[0]][arr[1]];
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
        //System.out.println( changeLimit);
        //System.out.println( midPoint);
        //System.out.println( "X: "+ arr[0] + " Y: "+ arr[1]);
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
    private double[] matrixToCoordinates(int x, int y) {
        double[] arr = new double[2];
        int midPoint = (int) (this.matrixSize / 2.0 - 0.5); // index of the middle of matrix. We assume this is (0 [m],0 [m]) in
        double changeLimit = (this.matrixSize - 1) / this.mapWidth; // calculates how much coordinate (x or y) must change to change the cell in matrix

        // Calculate coordinates
        arr[0] = (x - midPoint) / changeLimit;
        arr[1] = (y - midPoint) / -changeLimit;

        // Return the coordinates
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
       // System.out.println( "midPixelX"+ midPixelX+"midPixelY"+ midPixelY);

        arr[0] = midPixelX + (int) Math.round(x * scaleFactor);
        arr[1] = midPixelY - (int) Math.round(y * scaleFactor);

      //  System.out.println("base pixel coordinates"+ arr[0]+" "+ arr[1]);
        return arr;
    }

    /**
     * Generates basic map for the game
     * @return Pane - the map with  MappableObjects
     */
    private void createMap() {
        int numRows = matrixSize;
        int numCols = matrixSize;

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
                Color color = Color.GREEN;
                if (objectOnMap instanceof AreaType) {
                    color = ((AreaType) objectOnMap).getColor();

                } else if (objectOnMap instanceof Water) {
                    color = ((Water) objectOnMap).getColor();
                }
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
     * Adds MovableObject object to map. Scales it from physic unit [meters] to relative pixel size.
     * @param obj
     * @return Pane1
     */
    public  void addMovableObjectToMap (MovableObjects obj){
        Node visualRepresentation = obj.getVisualRepresentation();
        int[] pixelCoords = coordinatesToPixel(obj.getX(),obj.getY());
        visualRepresentation.setLayoutX(pixelCoords[0]-obj.getDistanceFromOrigin()*this.scaleFactor);
        visualRepresentation.setLayoutY(pixelCoords[1]-obj.getDistanceFromOrigin()*this.scaleFactor);
      //  System.err.println( obj.getDistanceFromOrigin()+" "+visualRepresentation.getLayoutX());
      //  System.err.println( obj.getDistanceFromOrigin()+" "+visualRepresentation.getLayoutY());
        visualRepresentation.setScaleX(this.scaleFactor);
        visualRepresentation.setScaleY(this.scaleFactor);
      //  System.out.println("distance from middle: "+obj.getDistanceFromOrigin()+"  after scaling: "+ obj.getDistanceFromOrigin()*this.scaleFactor+" final: "+(pixelCoords[1]-obj.getDistanceFromOrigin()*this.scaleFactor)+" scaling factor: "+this.scaleFactor);
      //  System.out.println(obj.getX()+" "+obj.getY());
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
        visualRepresentation.setLayoutX(pixelCoords[0]-obj.getDistanceFromOrigin()*this.scaleFactor);
        visualRepresentation.setLayoutY(pixelCoords[1]-obj.getDistanceFromOrigin()*this.scaleFactor);
        //System.out.println(" v: "+(pixelCoords[0]-obj.getDistanceFromOrigin()*this.scaleFactor)+" " + pixelCoords[0] +" - "+obj.getDistanceFromOrigin()*scaleFactor);

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
    // public void animateMovableObject(MovableObjects obj, CoordinatesPath coordinatesPath) {
    //     double[][] path = coordinatesPath.getPath();
    //     double timeInterval = 2.0/path[0].length;
    //     Node objVisualRepresentation = obj.getVisualRepresentation();
    //     // Creating a timeline for the animation
    //     Timeline timeline = new Timeline();
    //     for (int i = 0; i < path[0].length; i++) {
    //         double newX = path[0][i];
    //         double newY = path[1][i];
    //         // Updating encapsulated obj coordinates
    //         obj.setPositionX(newX);
    //         obj.setPositionY(newY);
    //         //Translating object so  the origin point matches right pixel
    //         int[] pixelCoords = coordinatesToPixel(newX,newY);
    //         double newX_pixel = pixelCoords[0]-obj.getDistanceFromOrigin()*this.scaleFactor;
    //         double newY_pixel = pixelCoords[1]-obj.getDistanceFromOrigin()*this.scaleFactor;
    //         Duration duration = Duration.seconds(timeInterval);
    //         timeline.getKeyFrames().add(
    //                 new KeyFrame(duration, new KeyValue(objVisualRepresentation.translateXProperty(), newX_pixel), new KeyValue(objVisualRepresentation.translateYProperty(), newY_pixel))
    //         );
    //     }
    //     timeline.play();
    // }

    //}

}