package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import javafx.scene.paint.Color;

/**
 * Groups  Area types present in game
 */
public interface AreaType extends MatrixMapArea {
    Color getColor();
    void setColor(Color color);
    double getKineticFriction();
    double getStaticFriction();

    void setKineticFriction(double kinetic_Friction);

    void setStaticFriction(double static_Friction);
}