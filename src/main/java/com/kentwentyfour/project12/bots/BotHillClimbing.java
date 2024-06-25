package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

/**
 * The BotHillClimbing class implements the BotPlayer interface to control a golf ball's path using a hill climbing algorithm.
 * It calculates paths aiming to reach a specified hole on the map, iterating through random velocities and adjusting based on the distance from the target.
 */
public class BotHillClimbing implements BotPlayer {
    private MapManager mapGenerator; // Manages the map in the game
    private PhysicsEngine physicsEngine; // Handles the physics calculations
    private ReferenceStore referenceStore = ReferenceStore.getInstance(); // Reference store for game resources
    private double targetX; // X coordinate of the target hole
    private double targetY; // Y coordinate of the target hole
    private long computationTime; // Computation time taken for the last path calculation
    private int numberOfTurns = 1; // Number of turns or iterations this bot will play

    /**
     * Constructs a new instance of BotHillClimbing.
     * Initializes the physics engine and map generator from the reference store.
     */
    public BotHillClimbing() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapGenerator = referenceStore.getMapManager();
    }

    /**
     * Calculates the path of the golf ball towards the specified target hole using hill climbing.
     *
     * @param golfBall The golf ball object representing the current position and state.
     * @param targetX  The X coordinate of the target hole.
     * @param targetY  The Y coordinate of the target hole.
     * @return A CoordinatesPath object representing the calculated path of the golf ball.
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
     * Checks the minimum distance of the calculated path from the target hole.
     *
     * @param coordinatesPath The CoordinatesPath object representing the path to check.
     * @return The minimum distance from the hole.
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
     * Implements the hill climbing algorithm to find the best path towards the target hole.
     *
     * @param golfBall The golf ball object representing the current position and state.
     * @return A CoordinatesPath object representing the best path found by the hill climbing algorithm.
     */
    private CoordinatesPath hillClimbing(GolfBall golfBall) {
        double bestDistance = Double.POSITIVE_INFINITY;
        CoordinatesPath bestPath = null;
        int maxIterations = 100;
        int restartLimit = 10;
        double initialStepSize = 0.5;
        double stepDecay = 0.99;
        double acceptableDistance = 0.15;

        double borderX = targetX - golfBall.getX();
        double borderY = targetY - golfBall.getY();

        for (int restart = 0; restart < restartLimit; restart++) {
            double[] velocity = {Math.random() * borderX, Math.random() * borderY};
            for (int i = 0; i < maxIterations; i++) {
                double stepSize = initialStepSize * Math.pow(stepDecay, i);
                double[] newVelocity = {
                        Math.min(Math.max(velocity[0] + (Math.random() * 2 - 1) * stepSize, -5), 5),
                        Math.min(Math.max(velocity[1] + (Math.random() * 2 - 1) * stepSize, -5), 5)
                };
                CoordinatesPath newPath = physicsEngine.calculateCoordinatePath(golfBall, newVelocity[0], newVelocity[1]);
                if (newPath == null) {
                    continue;
                }
                double newDistance = checkDistanceFromHole(newPath);

                if (newDistance < bestDistance) {
                    velocity = newVelocity;
                    bestPath = newPath;
                    bestDistance = newDistance;
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
     * Retrieves the computation time taken for the last path calculation.
     *
     * @return The computation time in nanoseconds.
     */
    @Override
    public long getComputationTime() {
        return computationTime;
    }

    /**
     * Retrieves the name of the bot.
     *
     * @return The name of the bot.
     */
    @Override
    public String getName() {
        return "BotHillClimbing";
    }

    /**
     * Retrieves the number of turns or iterations this bot will play.
     *
     * @return The number of turns or iterations.
     */
    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }
}
