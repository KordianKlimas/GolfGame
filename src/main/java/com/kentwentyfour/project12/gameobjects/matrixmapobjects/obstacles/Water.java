package com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles;

import javafx.scene.paint.Color;
public class Water implements ObstacleArea {

    private double coordinateX1=0;
    private double coordinateY1=0;
    private double widthShape=0;
    private double heightShape=0;
    private  double height;


    public Water(double height) {this.height = height;}
    public Water(double coordinateX1, double coordinateY1, double width, double height) {
        this.coordinateX1 = coordinateX1;
        this.coordinateY1 = coordinateY1;
        this.widthShape = width;
        this.heightShape = height;


    }
    @Override
    public Color getColor() {
        if(height <-0.1) {
            return Color.rgb(0,119,192);
        }
        else if (height <  -0.02) {
            return Color.rgb(23,143,215);
        }
         else if (height <=0) {
          return  Color.rgb(55,175,247);
        }
        return Color.rgb(0,119,192);
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
