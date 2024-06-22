package com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles;

import javafx.scene.paint.Color;
public class Water implements ObstacleArea {
    @Override
    public Color getColor() {
        return Color.BLUE;
    }
    private double coordinateX1=0;
    private double coordinateY1=0;
    private double widthShape=0;
    private double heightShape=0;
    private  double height;

    public Water(double height) {
        this.height = height;

    }
    public Water(double coordinateX1, double coordinateY1, double width, double height) {
        this.coordinateX1 = coordinateX1;
        this.coordinateY1 = coordinateY1;
        this.widthShape = width;
        this.heightShape = height;
    }

    // Getters
    public double getCoordinateX1() {
        return coordinateX1;
    }

    public double getCoordinateY1() {
        return coordinateY1;
    }

    public double getShapeWidth() {
        return widthShape;
    }

    public double getShapeHeight() {
        return heightShape;
    }
}
