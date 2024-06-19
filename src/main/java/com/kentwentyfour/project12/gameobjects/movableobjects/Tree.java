package com.kentwentyfour.project12.gameobjects.movableobjects;

import javafx.scene.Node;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;

public class Tree implements  ReboundingObstacle {
    static double DEFAULT_RESTITUTION_COEFFICIENT=0.97; // the amount left from both velocities [m/s] after hit
    private double coordinate_X;
    private double coordinate_Y;
    private double radius;
    private Image nodeImage;
    public Tree(double coordinate_X,double coordinate_Y,double radius){
        this.coordinate_X = coordinate_X;
        this.coordinate_Y = coordinate_Y;
        this.radius = radius;
        this.nodeImage = new Image("file:src/main/java/com/kentwentyfour/project12/gameobjects/textures/tree_1.png");

    }
    public Node getVisualRepresentation() {
        Circle circle = new Circle(coordinate_X, coordinate_Y, radius);
        circle.setFill(new ImagePattern(nodeImage)); // Set image as fill

        return circle;
    }

    public double getDistanceFromOrigin() {
        return radius;
    }

    public void setPositionX(double x) {
        this.coordinate_X = x;
    }

    public void setPositionY(double y) {
        this.coordinate_Y = y;
    }

    public double getX() {
        return coordinate_X;
    }

    public double getY() {
        return coordinate_Y;
    }



    public double getRestitutionCoefficient() {
        return DEFAULT_RESTITUTION_COEFFICIENT;
    }
}
