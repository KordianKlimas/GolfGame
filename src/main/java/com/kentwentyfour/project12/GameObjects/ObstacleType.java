package com.kentwentyfour.project12.GameObjects;

import javafx.scene.paint.Color;

/**
 * Groups  all obstacles that can be displayed on map.
 */
public interface ObstacleType extends MappableObject {
    Color getColor();
}
