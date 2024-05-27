package com.kentwentyfour.project12.gameobjects.matrixmapobjects.areaobstacles;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import javafx.scene.paint.Color;

/**
 * Groups  all obstacles that can be displayed on map.
 */
public interface ObstacleType extends MatrixMapArea {
    Color getColor();
}
