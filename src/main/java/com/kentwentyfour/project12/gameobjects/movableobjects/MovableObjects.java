package com.kentwentyfour.project12.gameobjects.movableobjects;

import javafx.scene.Node;

/**
 * Interface for objects which can be displayed on top of map and change their position
 */
public interface MovableObjects {
    public  Node getVisualRepresentation();
    /**
     *Returns distance from origin ( middle ) of the object (in meters )
     * @return double distance
     */
    public double getDistanceFromOrigin();
    public  void setPositionX(double x);
    public  void setPositionY(double y);
    public double  getX();
    public double  getY();
}
