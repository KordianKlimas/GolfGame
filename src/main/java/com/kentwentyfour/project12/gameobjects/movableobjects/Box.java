package com.kentwentyfour.project12.gameobjects.movableobjects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Box implements MovableObjects , ReboundingObstacle {
    static double DEFAULT_RESTITUTION_COEFFICIENT=0.97; // the amount left from both velocities [m/s] after hit
    private double coordinate_X;
    private double coordinate_Y;
    private double radius;
    public Box(double coordinate_X, double coordinate_Y, double radius){
        this.coordinate_X = coordinate_X;
        this.coordinate_Y = coordinate_Y;
        this.radius = radius;
    }
    public Node getVisualRepresentation() {
        Rectangle square = new Rectangle(coordinate_X, coordinate_Y, 2, 2);
        square.setFill(Color.DARKGRAY);
        return square;
    }

    public double getDistanceFromOrigin() {
        return radius/2;
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

    public Color getColor() {
        return Color.SADDLEBROWN;
    }

    public double getRestitutionCoefficient() {
        return DEFAULT_RESTITUTION_COEFFICIENT;
    }
}
