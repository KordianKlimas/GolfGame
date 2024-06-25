package com.kentwentyfour.project12.bots.improvedbot;


import com.kentwentyfour.project12.bots.BasicBot;
import com.kentwentyfour.project12.bots.BotPlayer;
import com.kentwentyfour.project12.bots.MultipleTurnBot;
import com.kentwentyfour.project12.bots.improvedbot.resources.AStarAlgorithm;
import com.kentwentyfour.project12.bots.improvedbot.resources.SecondAlgorithm;
import com.kentwentyfour.project12.bots.improvedbot.resources.Waypoint;
import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MazeBot implements BotPlayer, MultipleTurnBot {
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private double  targetX;
    private double targetY;
    private GolfBall ball;
    private List<Waypoint> aStarPath;
    private int count = 0;

    private long computationTime;
    private int numberOfTurns = 1;

    public MazeBot() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapManager = referenceStore.getMapManager();
        ArrayList<GolfBall> golfBallArrayList = referenceStore.getGolfballList();
        this.ball = golfBallArrayList.getFirst() ;
    }
    @Override
    public CoordinatesPath calculatePath(GolfBall golfBall,double targetX,double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path = null;
        BasicBot usedBot= new BasicBot();
        path = usedBot.calculatePath(golfBall,targetX,targetY);
        long endTime = System.nanoTime();
        computationTime = endTime - startTime;
        return path;
    }

    public void generateWaypointPath(double targetX, double targetY){
        // decrease range for more midpoints 1=max 100=min
        this.targetX = targetX;
        this.targetY = targetY;
        SecondAlgorithm astarAlgorithm = new SecondAlgorithm();
        this.aStarPath = astarAlgorithm.generateWaypoints(ball.getX(), ball.getY(), this.targetX, this.targetY);
        if(this.aStarPath == null || this.aStarPath.isEmpty()){
            System.err.println("No waypoints created");
        }
        for (Waypoint waypoint : aStarPath) {
            System.out.println("Waypoint: " + waypoint);
        }
        mapManager.drawAStarPath(aStarPath);
    }
    public List<Waypoint> getCurrentWaypointPath(){
        return this.aStarPath;
    }

    @Override
    public long getComputationTime() {
        return computationTime;
    }

    @Override
    public String getName() {
        return "BasicBot";
    }

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }
}
