package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

public class BotHillClimbing implements BotPlayer {
    private MapManager mapGenerator;
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private double targetX;
    private double targetY;
    private long computationTime;
    private int numberOfTurns = 1;

    public BotHillClimbing() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapGenerator = referenceStore.getMapManager();
    }
    public CoordinatesPath calculatePath(GolfBall golfBall,double targetX, double targetY) {
        long startTime = System.nanoTime();
        this.targetX = targetX;
        this.targetY = targetY;
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
    public double checkDistanceFromHole(CoordinatesPath coordinatesPath) {
        double[][] path = coordinatesPath.getPath();

        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path[0].length; i++) {
            double ballX = path[0][i];
            double ballY = path[1][i];

            double distanceSquared = (ballX - targetX) * (ballX - targetX) + (ballY - targetY) * (ballY - targetY);
            minDistanceSquared = Math.min(minDistanceSquared, distanceSquared);
        }
        return Math.sqrt(minDistanceSquared);
    }
    private CoordinatesPath hillClimbing(GolfBall golfBall){

        double bestDistance = Double.POSITIVE_INFINITY;
        CoordinatesPath bestPath = null;
        int max = 100;
        int restartLimit = 10;
        double initialStepSize = 0.5;
        double stepDecay = 0.99;
        double acceptableDistance = 0.15;

        double BorderX =targetX - golfBall.getX();
        double BorderY =targetY - golfBall.getY();

        for (int restart = 0; restart< restartLimit;restart++) {
            double[] veloc = {(Math.random() * BorderX), (Math.random() * BorderY)};
            for (int i = 0; i < max; i++) {
                double stepSize = initialStepSize * Math.pow(stepDecay, i);
                double[] newVeloc = {
                        Math.min(Math.max(veloc[0] + (Math.random() * 2 - 1) * stepSize, -5), 5),
                        Math.min(Math.max(veloc[1] + (Math.random() * 2 - 1) * stepSize, -5), 5)
                };
                CoordinatesPath newPath = physicsEngine.calculateCoordinatePath(golfBall, newVeloc[0], newVeloc[1]);
                if (newPath == null) {
                    //  System.err.println("Iteration " + i + ": New path is null.");
                    continue;
                }
                double newDis = checkDistanceFromHole(newPath);
                // System.out.println("Iteration " + i + ": New path calculated. Distance: " + newDis);

                if (newDis < bestDistance) {
                    veloc = newVeloc;
                    bestPath = newPath;
                    bestDistance = newDis;
                    // System.out.println("Iteration " + i + ": Best path updated. Best Distance: " + bestDistance);
                    if (bestDistance<acceptableDistance){
                        //    System.out.print("Acceptable distance reached.");
                        return bestPath;
                    }
                }
            }
            if (bestPath != null) {
                break;
            }
        }
        if (bestPath ==null){
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
        return "BotHillClimbing";
    }
    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }
}