package com.kentwentyfour.project12.GameObjects;

import javafx.scene.paint.Color;

/**
 * Groups  Area types present in game
 */
public interface AreaType extends MappableObject{
    Color getColor();
    void setColor(Color color);
    double getKineticFriction();
    double getStaticFriction();

    void setKineticFriction(double kinetic_Friction);

    void setStaticFriction(double static_Friction);
}