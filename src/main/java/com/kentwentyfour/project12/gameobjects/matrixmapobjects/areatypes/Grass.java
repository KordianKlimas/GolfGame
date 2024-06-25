package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import javafx.scene.paint.Color;

/**
 * The Grass class implements the AreaType interface, representing a grass area type
 * with specific kinetic and static friction coefficients and a color based on height.
 */
public class Grass implements AreaType {
    /** Default kinetic friction coefficient for grass. */
    public static double kinetic_Friction = 0.1;

    /** Default static friction coefficient for grass. */
    public static double static_Friction = 0.3;

    /** Color of the grass area. */
    private Color color;

    /**
     * Constructs a Grass object with a color determined by the specified height.
     *
     * @param height the height that determines the color of the grass.
     */
    public Grass(double height) {
        if (height < 0.5) {
            this.color = Color.rgb(65, 152, 10);
        } else if (height < 0.7) {
            this.color = Color.rgb(38, 139, 7);
        } else if (height < 0.9) {
            this.color = Color.rgb(19, 133, 16);
        } else if (height < 1.1) {
            this.color = Color.rgb(17, 124, 19);
        } else if (height < 1.3) {
            this.color = Color.rgb(14, 105, 16);
        } else if (height < 1.5) {
            this.color = Color.rgb(11, 86, 13);
        } else if (height < 1.7) {
            this.color = Color.rgb(8, 67, 10);
        } else if (height < 1.9) {
            this.color = Color.rgb(9, 39, 10);
        } else if (height < 2.1) {
            this.color = Color.rgb(9, 30, 10);
        } else {
            this.color = Color.rgb(9, 30, 10);
        }
    }

    /**
     * Returns the kinetic friction coefficient of the grass.
     *
     * @return the kinetic friction coefficient.
     */
    @Override
    public double getKineticFriction() {
        return kinetic_Friction;
    }

    /**
     * Returns the static friction coefficient of the grass.
     *
     * @return the static friction coefficient.
     */
    @Override
    public double getStaticFriction() {
        return static_Friction;
    }

    /**
     * Sets the kinetic friction coefficient of the grass.
     *
     * @param kinetic_Friction the kinetic friction coefficient to set.
     */
    @Override
    public void setKineticFriction(double kinetic_Friction) {
        Grass.kinetic_Friction = kinetic_Friction;
    }

    /**
     * Sets the static friction coefficient of the grass.
     *
     * @param static_Friction the static friction coefficient to set.
     */
    @Override
    public void setStaticFriction(double static_Friction) {
        Grass.static_Friction = static_Friction;
    }

    /**
     * Returns the color of the grass.
     *
     * @return the color of the grass.
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the color of the grass.
     *
     * @param color the color to set.
     */
    @Override
    public void setColor(Color color) {
        if (color.toString().equals("0x000000ff")) {
            System.out.println("black");
        }
        this.color = color;
    }
}
