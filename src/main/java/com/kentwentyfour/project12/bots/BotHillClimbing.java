package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

/**
 * The BotHillClimbing class implements a bot player for a golf game simulation using the hill climbing algorithm.
 * It calculates the path and velocity needed for a GolfBall object to reach a target.
 */
public class BotHillClimbing implements BotPlayer {

    private MapManager mapGenerator;
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private double targetX;
    private double targetY;
    private long computationTime;
    private int numberOfTurns = 1;

    /**
     * Default constructor for BotHillClimbing.
     */
    public BotHillClimbing() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapGenerator = referenceStore.getMapManager();
    }

    /**
     * Calculates the path for the GolfBall to reach the target coordinates using the hill climbing algorithm.
     *
     * @param golfBall the GolfBall object.
     * @param targetX the X coordinate of the target.
     * @param targetY the Y coordinate of the target.
     * @return the calculated path as a CoordinatesPath object.
     */
    public CoordinatesPath calculatePath(GolfBall golfBall, double targetX, double targetY) {
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

    /**
     * Checks the minimum distance from the calculated path to the target coordinates.
     *
     * @param coordinatesPath the calculated path.
     * @return the minimum distance to the target.
     */
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

    /**
     * Implements the hill climbing algorithm to find the optimal path for the GolfBall.
     *
     * @param golfBall the GolfBall object.
     * @return the calculated path as a CoordinatesPath object, or null if no valid path is found.
     */
    private CoordinatesPath hillClimbing(GolfBall golfBall) {
        double bestDistance = Double.POSITIVE_INFINITY;
        CoordinatesPath bestPath = null;
        int max = 100;
        int restartLimit = 10;
        double initialStepSize = 0.5;
        double stepDecay = 0.99;
        double acceptableDistance = 0.15;

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
                    continue;
                }
                double newDis = checkDistanceFromHole(newPath);

                if (newDis < bestDistance) {
                    veloc = newVeloc;
                    bestPath = newPath;
                    bestDistance = newDis;
                    if (bestDistance < acceptableDistance) {
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

    /**
     * Gets the computation time for the last path calculation.
     *
     * @return the computation time in nanoseconds.
     */
    @Override
    public long getComputationTime() {
        return computationTime;
    }

    /**
     * Gets the name of the bot.
     *
     * @return the name of the bot.
     */
    @Override
    public String getName() {
        return "BotHillClimbing";
    }

    /**
     * Gets the number of turns taken by the bot.
     *
     * @return the number of turns.
     */
    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }
}
