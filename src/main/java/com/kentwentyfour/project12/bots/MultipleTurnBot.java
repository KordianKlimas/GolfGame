package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.bots.improvedbot.resources.Waypoint;

import java.util.List;

public interface MultipleTurnBot extends BotPlayer{
    public void generateWaypointPath(double targetX, double targetY);
    public List<Waypoint> getCurrentWaypointPath();

}
