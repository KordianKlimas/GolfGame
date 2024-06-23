package com.kentwentyfour.project12.bots.improvedbot;


import com.kentwentyfour.project12.bots.BotPlayer;
import com.kentwentyfour.project12.bots.MultipleTurnBot;
import com.kentwentyfour.project12.bots.improvedbot.resources.AStarAlgorithm;
import com.kentwentyfour.project12.bots.improvedbot.resources.Waypoint;
import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
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

        this.targetX = targetX;
        this.targetY = targetY;

        long startTime = System.nanoTime();

        if (count < aStarPath.size() - 1) {
            count++;
        }
        CoordinatesPath path = hillClimbing(golfBall);
        if (path == null) {
            System.err.println("Hill climbing did not find a valid path.");
        } else {
            System.out.println("Hill climbing found a path.");
        }

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;

        return path;
    }

    public void genereteWaypointPath(double targetX, double targetY){
        // decrease range for more midpoints 1=max 100=min
        this.targetX = targetX;
        this.targetY = targetY;
        AStarAlgorithm astarAlgorithm = new AStarAlgorithm();
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


    public double checkDistanceFromHole(CoordinatesPath coordinatesPath) {
        double[][] path = coordinatesPath.getPath();

        Waypoint targetWaypoint = aStarPath.get(count);
        double holeX = targetWaypoint.x;
        double holeY = targetWaypoint.y;

        System.out.println("Checking path for " + holeX + " " + holeY);

        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path[0].length; i++) {
            double ballX = path[0][i];
            double ballY = path[1][i];

            double distanceSquared = (ballX - holeX) * (ballX - holeX) + (ballY - holeY) * (ballY - holeY);
            minDistanceSquared = Math.min(minDistanceSquared, distanceSquared);
        }
        return Math.sqrt(minDistanceSquared);
    }

    private CoordinatesPath hillClimbing(GolfBall golfBall) {
        double bestDistance = Double.POSITIVE_INFINITY;
        CoordinatesPath bestPath = null;
        int max = 100;
        int restartLimit = 10;
        double initialStepSize = 0.5;
        double stepDecay = 0.99;
        double acceptableDistance = 0.15;

        Waypoint targetWaypoint = aStarPath.get(count);
        double targetX = targetWaypoint.x;
        double targetY = targetWaypoint.y;

        double BorderX = targetX - golfBall.getX();
        double BorderY = targetY - golfBall.getY();

        for (int restart = 0; restart < restartLimit; restart++) {
            double[] veloc = {(Math.random() * BorderX), (Math.random() * BorderY)};
            for (int i = 0; i < max; i++) {
                double stepSize = initialStepSize * Math.pow(stepDecay, i);
                double[] newVeloc = {
                        Math.min(Math.max(veloc[0] + (Math.random() * 2 - 1) * stepSize, -5), 5),
                        Math.min(Math.max(veloc[1] + (Math.random() * 2 - 1) * stepSize, -5), 5)
                };
                CoordinatesPath newPath = physicsEngine.calculateCoordinatePath(golfBall, newVeloc[0], newVeloc[1]);
                if (newPath == null) {
                    // System.err.println("Iteration " + i + ": New path is null.");
                    continue;
                }
                double newDis = checkDistanceFromHole(newPath);
                // System.out.println("Iteration " + i + ": New path calculated. Distance: " + newDis);

                if (newDis < bestDistance) {
                    veloc = newVeloc;
                    bestPath = newPath;
                    bestDistance = newDis;
                    // System.out.println("Iteration " + i + ": Best path updated. Best Distance: " + bestDistance);
                    if (bestDistance < acceptableDistance) {
                        // System.out.print("Acceptable distance reached.");
                        return bestPath;
                    }
                }
            }
            if (bestPath != null) {
                break;
            }
        }
        if (bestPath == null) {
            System.err.println("Hill climbing did not find a valid path.");
        }
        return bestPath;
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
