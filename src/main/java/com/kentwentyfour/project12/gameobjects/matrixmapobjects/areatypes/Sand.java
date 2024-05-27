package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import javafx.scene.paint.Color;

public class Sand implements AreaType {
    public static double kinetic_Friction = 0.1; // default value
    public static double static_Friction = 0.3; // default value

    private Color color = Color.SANDYBROWN;

    @Override
    public Color getColor() {
        return color;
    }

    public void setColor(Color color){
        this.color=color;
    }

    @Override
    public double getKineticFriction() {
        return kinetic_Friction;
    }

    @Override
    public double getStaticFriction() {
        return static_Friction;
    }
    @Override
    public void setKineticFriction(double kinetic_Friction) {
        this.kinetic_Friction = kinetic_Friction;
    }
    @Override
    public void setStaticFriction(double static_Friction) {
        this.static_Friction = static_Friction;
    }

}