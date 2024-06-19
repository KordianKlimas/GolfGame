package com.kentwentyfour.project12.gameobjects.movableobjects;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;

/**
 * Groups obstacles that can bounce of the ball.
 */
public interface ReboundingObstacle extends MovableObjects {
    public double getRestitutionCoefficient();
}
