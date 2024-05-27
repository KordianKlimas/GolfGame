package com.kentwentyfour.project12.GameObjects.Obstacles;

import com.kentwentyfour.project12.GameObjects.ObstacleType;
import javafx.scene.paint.Color;
public class Water implements ObstacleType {
    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
