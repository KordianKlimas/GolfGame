package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import javafx.scene.paint.Color;


public class Grass implements AreaType {
    public static double kinetic_Friction = 0.1; // default value
    public static double static_Friction = 0.3; // default value
    private Color color;

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

    public Grass(double height) {
        if (height < 0.5) {
            this.color = Color.rgb(65,152,10);
        } else if (height < 0.7) {
            this.color = Color.rgb(38,139,7);
        }  else if (height < 0.9) {
            this.color = Color.rgb(19,133,16);
        } else if (height < 1.1) {
            this.color = Color.rgb(17,124,19);
        }  else if (height < 1.3) {
            this.color = Color.rgb(14, 105, 16); // Darker green
        } else if (height < 1.5) {
            this.color = Color.rgb(11, 86, 13); // Darker green
        } else if (height < 1.7) {
            this.color = Color.rgb(8, 67, 10); // Darker green
        }   else if(height < 1.9) {
            this.color = Color.rgb(9, 39, 10); // Darker green
        }else {
            this.color = Color.rgb(9, 30, 10); // Darker green
        }
    }
    @Override
    public Color getColor() {
        return this.color;
    }
    @Override
    public void setColor(Color color) {
        if(color.toString().equals("0x000000ff") ){
            System.out.println("blac");
        }
        this.color = color;
    }

}