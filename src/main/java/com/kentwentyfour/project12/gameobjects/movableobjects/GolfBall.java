package com.kentwentyfour.project12.gameobjects.movableobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GolfBall implements MovableObjects {
    private double coordinate_X;
    private double coordinate_Y;
    private double mass;
    private double radius;
    private Circle visualRepresentation;
    /**
     * Creates ball object
     * @param coordinate_X
     * @param coordinate_Y
     * @param mass
     */
    public GolfBall(double coordinate_X, double coordinate_Y, double mass,double radius){
        this.coordinate_X = coordinate_X;
        this.coordinate_Y = coordinate_Y;
        this.mass = mass;
        this.radius = radius;
        this.visualRepresentation = createVisualRepresentation();
    }


    public double getDistanceFromOrigin(){
        // Circle drawn by middle coordinates
        return 0;
    }
    /**
     * Create a graphical representation of the ball
     * @return Circle representing the ball
     */
    private Circle createVisualRepresentation() {
       Circle circle = new Circle(coordinate_X, coordinate_Y, radius);
       circle.setFill(Color.WHITE); // Set color of the ball
       return circle;
    }
    /**
     * Get the graphical representation of the ball
     * @return Circle representing the ball
     */
    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }
    //setters
    /**
     * Sets coordinates X and Y of ball
     * @param coordinate_X
     * @param coordinate_Y
     */
    public void setPosition(double coordinate_X,double coordinate_Y){
        this.coordinate_X = coordinate_X;
        this.coordinate_Y = coordinate_Y;
    }
    /**
     * Sets coordinate X
     * @param coordinate_X
     */
    public void setPositionX(double coordinate_X){
        this.coordinate_X = coordinate_X;
    }
    /**
     * Sets coordinate Y
     * @param coordinate_Y
     */
    public void setPositionY(double coordinate_Y){
        this.coordinate_Y = coordinate_Y;
    }
    /**
     * Sets mass
     * @param mass
     */
    public void setMass(double mass){
        this.mass = mass;
    }
    //getters
    /**
     *  Returns Coordinate X
     * @return double
     */
    public double getX() {
        return coordinate_X;
    }
    /**
     *  Returns Coordinates Y
     * @return double
     */
    public double getY() {
        return coordinate_Y;
    }
    /**
     *  Returns mass of ball
     * @return double
     */
    public double getRadius() {
        return this.radius;
    }
    /**
     *  Returns mass of ball
     * @return double
     */
    public double getMass() {
        return mass;
    }
}