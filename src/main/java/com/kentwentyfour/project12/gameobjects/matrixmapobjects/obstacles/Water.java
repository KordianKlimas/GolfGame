package com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles;

import javafx.scene.paint.Color;
public class Water implements ObstacleArea {
    @Override
    public Color getColor() {
        return Color.BLUE;
    }
    private double coordinateX1=0;
    private double coordinateY1=0;
    private double width=0;
    private double height=0;

    public Water() {}
    public Water(double coordinateX1, double coordinateY1, double width, double height) {
        this.coordinateX1 = coordinateX1;
        this.coordinateY1 = coordinateY1;
        this.width = width;
        this.height = height;
    }

    // Getters
    public double getCoordinateX1() {
        return coordinateX1;
    }

    public double getCoordinateY1() {
        return coordinateY1;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
