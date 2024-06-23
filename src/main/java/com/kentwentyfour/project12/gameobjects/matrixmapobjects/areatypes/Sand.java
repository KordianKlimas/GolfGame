package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import javafx.scene.paint.Color;

public class Sand implements AreaType {
    public static double kinetic_Friction = 0.1; // default value
    public static double static_Friction = 0.3; // default value
    private Color color;

    public Sand(double height){
        this.color = Color.rgb(225,191,146);
        if (height < 0.05) {
            this.color = Color.rgb(246,215,176);
        }
        else if (height < 0.1) {
            this.color = Color.rgb(242,210,169);
        }
        else if (height < 0.15) {
            this.color = Color.rgb	(236,204,162);
        } else if (height < 0.2) {
            this.color = Color.rgb(231,196,150);
        }
    }
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