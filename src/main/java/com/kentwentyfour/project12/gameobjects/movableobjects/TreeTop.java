package com.kentwentyfour.project12.gameobjects.movableobjects;


import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TreeTop implements MovableObjects {
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
     * @param treeRadius - based hole radius,  height of flag will be set
     */
    public TreeTop(double coordinate_X, double coordinate_Y, double treeRadius){
        this.width = treeRadius*2 + treeRadius/0.5;
        this.height = width/0.7;
        this.coordinate_X = coordinate_X + width/2 - 0.5*width;
        this.coordinate_Y = coordinate_Y + height/2  - treeRadius;

        this.nodeImage = new Image("file:src/main/java/com/kentwentyfour/project12/gameobjects/textures/restOfTree.png");
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

