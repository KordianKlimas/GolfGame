package com.kentwentyfour.project12.gameobjects.movableobjects;


import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Flag implements MovableObjects {
    private double coordinate_X;
    private double coordinate_Y;

    private Rectangle visualRepresentation;
    private Image nodeImage;
    private double height;
    private double width;
    /**
     * Creates Flag
     *
     * @param coordinate_X
     * @param coordinate_Y
     * @param holeRadius - based hole radius,  height of flag will be set
     */
    public Flag(double coordinate_X, double coordinate_Y, double holeRadius){
        this.height = 2 * holeRadius*2;
        this.width = height *0.55;
        this.coordinate_X = coordinate_X + width/2;
        this.coordinate_Y = coordinate_Y + height/2 ;//- height;


        this.nodeImage = new Image("file:src/main/java/com/kentwentyfour/project12/gameobjects/textures/Flag.png");
        this.visualRepresentation = createVisualRepresentation();
    }



    public double getDistanceFromOrigin(){
        return     Math.sqrt(height*height + width*width)/2;
    }
    /**
     * Create a graphical representation of the ball
     * @return Circle representing the ball
     */
    private Rectangle createVisualRepresentation() {
        Rectangle rectangle = new Rectangle(coordinate_X, coordinate_Y, width, height);
        this.nodeImage = new Image("file:src/main/java/com/kentwentyfour/project12/gameobjects/textures/flag.png");

        rectangle.setFill(new ImagePattern(nodeImage));

        return rectangle;
    }
    /**
     * Get the graphical representation of the ball
     * @return Circle representing the ball
     */
    public Rectangle getVisualRepresentation() {
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

}

