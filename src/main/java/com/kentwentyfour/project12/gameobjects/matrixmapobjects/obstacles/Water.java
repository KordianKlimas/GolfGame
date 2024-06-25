package com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles;

import javafx.scene.paint.Color;

/**
 * The Water class implements the ObstacleArea interface, representing a water obstacle area
 * with specific coordinates, dimensions, and a color based on height.
 */
public class Water implements ObstacleArea {

    /** The X coordinate of the water area. */
    private double coordinateX1 = 0;

    /** The Y coordinate of the water area. */
    private double coordinateY1 = 0;

    /** The width of the water area shape. */
    private double widthShape = 0;

    /** The height of the water area shape. */
    private double heightShape = 0;

    /** The height determining the color of the water. */
    private double height;

    /**
     * Constructs a Water object with the specified height.
     *
     * @param height the height that determines the color of the water.
     */
    public Water(double height) {
        this.height = height;
    }

    /**
     * Constructs a Water object with the specified coordinates and dimensions.
     *
     * @param coordinateX1 the X coordinate of the water area.
     * @param coordinateY1 the Y coordinate of the water area.
     * @param width        the width of the water area shape.
     * @param height       the height of the water area shape.
     */
    public Water(double coordinateX1, double coordinateY1, double width, double height) {
        this.coordinateX1 = coordinateX1;
        this.coordinateY1 = coordinateY1;
        this.widthShape = width;
        this.heightShape = height;
    }

    /**
     * Returns the color of the water based on its height.
     *
     * @return the color of the water.
     */
    @Override
    public Color getColor() {
        if (height < -0.1) {
            return Color.rgb(0, 119, 192);
        } else if (height < -0.02) {
            return Color.rgb(23, 143, 215);
        } else if (height <= 0) {
            return Color.rgb(55, 175, 247);
        }
        return Color.rgb(0, 119, 192);
    }

    /**
     * Returns the X coordinate of the water area.
     *
     * @return the X coordinate.
     */
    public double getCoordinateX1() {
        return coordinateX1;
    }

    /**
     * Returns the Y coordinate of the water area.
     *
     * @return the Y coordinate.
     */
    public double getCoordinateY1() {
        return coordinateY1;
    }

    /**
     * Returns the width of the water area shape.
     *
     * @return the width of the water area shape.
     */
    public double getShapeWidth() {
        return widthShape;
    }

    /**
     * Returns the height of the water area shape.
     *
     * @return the height of the water area shape.
     */
    public double getShapeHeight() {
        return heightShape;
    }
}
