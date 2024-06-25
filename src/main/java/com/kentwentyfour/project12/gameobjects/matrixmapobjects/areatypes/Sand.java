package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import javafx.scene.paint.Color;

/**
 * The Sand class implements the AreaType interface, representing a sand area type
 * with specific kinetic and static friction coefficients and a color based on height.
 */
public class Sand implements AreaType {
    /** Default kinetic friction coefficient for sand. */
    public static double kinetic_Friction = 0.1;

    /** Default static friction coefficient for sand. */
    public static double static_Friction = 0.3;

    /** Color of the sand area. */
    private Color color;

    /**
     * Constructs a Sand object with a color determined by the specified height.
     *
     * @param height the height that determines the color of the sand.
     */
    public Sand(double height) {
        this.color = Color.rgb(225, 191, 146);
        if (height < 0.05) {
            this.color = Color.rgb(246, 215, 176);
        } else if (height < 0.1) {
            this.color = Color.rgb(242, 210, 169);
        } else if (height < 0.15) {
            this.color = Color.rgb(236, 204, 162);
        } else if (height < 0.2) {
            this.color = Color.rgb(231, 196, 150);
        }
    }

    /**
     * Returns the color of the sand.
     *
     * @return the color of the sand.
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the sand.
     *
     * @param color the color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the kinetic friction coefficient of the sand.
     *
     * @return the kinetic friction coefficient.
     */
    @Override
    public double getKineticFriction() {
        return kinetic_Friction;
    }

    /**
     * Returns the static friction coefficient of the sand.
     *
     * @return the static friction coefficient.
     */
    @Override
    public double getStaticFriction() {
        return static_Friction;
    }

    /**
     * Sets the kinetic friction coefficient of the sand.
     *
     * @param kinetic_Friction the kinetic friction coefficient to set.
     */
    @Override
    public void setKineticFriction(double kinetic_Friction) {
        Sand.kinetic_Friction = kinetic_Friction;
    }

    /**
     * Sets the static friction coefficient of the sand.
     *
     * @param static_Friction the static friction coefficient to set.
     */
    @Override
    public void setStaticFriction(double static_Friction) {
        Sand.static_Friction = static_Friction;
    }
}
