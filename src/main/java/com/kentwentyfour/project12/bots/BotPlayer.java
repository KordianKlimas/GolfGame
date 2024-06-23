package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;

/**
 * Groups all bots
 */
public interface BotPlayer {
    public CoordinatesPath calculatePath(GolfBall golfball,double coordinateX, double coordinateY);
    public long getComputationTime();
    public String getName();
    public int getNumberOfTurns();
}
