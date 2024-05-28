package com.kentwentyfour.project12.Bots;

import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;

public interface BotPlayer {
    public CoordinatesPath calculatePath(GolfBall golfball);
}
