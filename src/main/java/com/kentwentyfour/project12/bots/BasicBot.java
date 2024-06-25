package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.Arrays;

/**
 * The BasicBot class implements a bot player for a golf game simulation.
 * It calculates the path and velocity needed for a GolfBall object to reach a target.
 * This involves using a physics engine to simulate the trajectory considering factors like slope and friction.
 */
public class BasicBot implements BotPlayer {

    private static final double thresholdDistance = 0.1;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private PhysicsEngine physicsEngine = referenceStore.getPhysicsEngine();
    private Hole hole = referenceStore.getHole();

    private long computationTime;
    private int numberOfTurns = 1;

    private double targetX;
    private double targetY;

    /**
     * Default constructor for BasicBot.
     */
    public BasicBot() {}

    /**
     * Calculates the path for the GolfBall to reach the target coordinates.
     *
     * @param golfBall the GolfBall object.
     * @param targetX the X coordinate of the target.
     * @param targetY the Y coordinate of the target.
     * @return the calculated path as a CoordinatesPath object.
     */
    public CoordinatesPath calculatePath(GolfBall golfBall, double targetX, double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path;
        this.targetX = targetX;
        this.targetY = targetY;

        double[] direction = calculateDirection(golfBall, targetX, targetY);
        double angleDegrees = calculateAngleDegree(direction, targetY, targetX, golfBall);
        double angleRadians = calculateAngleRadian(angleDegrees);

        double[] velocities = calculateVelocities(angleRadians, direction, golfBall);
        double velocityX = velocities[0];
        double velocityY = velocities[1];

        path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
        double distance = checkDistanceFromHole(path);
        if (distance > thresholdDistance) {
            velocityX *= 10;
            velocityY *= 10;
            path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
        }

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;
        System.err.println(Arrays.deepToString(path.getPath()));
        return path;
    }

    /**
     * Calculates the direction vector from the GolfBall to the target coordinates.
     *
     * @param golfBall the GolfBall object.
     * @param targetX the X coordinate of the target.
     * @param targetY the Y coordinate of the target.
     * @return the direction vector as an array of two doubles.
     */
    public double[] calculateDirection(GolfBall golfBall, double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        double distancex = Math.abs(targetX - golfBall.getX());
        double distancey = Math.abs(targetY - golfBall.getY());
        double magnitude = Math.sqrt(distancex * distancex + distancey * distancey);
        double dhdx = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dx", golfBall.getX(), golfBall.getY());
        double dhdy = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dy", golfBall.getX(), golfBall.getY());
        double slopeFactor = dhdx * distancex / magnitude + dhdy * distancey / magnitude;
        double adjustedDistancex = Math.abs(distancex - slopeFactor * dhdx);
        double adjustedDistancey = Math.abs(distancey - slopeFactor * dhdy);
        magnitude = Math.sqrt(adjustedDistancex * adjustedDistancex + adjustedDistancey * adjustedDistancey);
        double[] direction = {adjustedDistancex / magnitude, adjustedDistancey / magnitude};

        return direction;
    }

    /**
     * Calculates the velocities required to reach the target.
     *
     * @param angleRad the angle in radians.
     * @param directions the direction vector.
     * @param golfBall the GolfBall object.
     * @return the velocities as an array of two doubles.
     */
    public double[] calculateVelocities(double angleRad, double[] directions, GolfBall golfBall) {
        double slopeFactor = calculateSlopeFactor(golfBall.getX(), golfBall.getY());
        double v = Math.sqrt(directions[0] * directions[0] + directions[1] * directions[1]);
        double friction = referenceStore.getFrictionCoefficient();
        v = v * slopeFactor * friction;
        double vx = v * Math.cos(angleRad);
        double vy = v * Math.sin(angleRad);
        double[] velocities = {vx, vy};
        return velocities;
    }

    /**
     * Calculates the slope factor based on the derivatives of the height at the given coordinates.
     *
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @return the slope factor.
     */
    private double calculateSlopeFactor(double x, double y) {
        double dhdx = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dx", x, y);
        double dhdy = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dy", x, y);
        double slopeMagnitude = Math.sqrt(dhdx * dhdx + dhdy * dhdy);
        return 1.0 - slopeMagnitude;
    }

    /**
     * Calculates the angle in degrees based on the direction vector and the coordinates.
     *
     * @param direction the direction vector.
     * @param targetY the Y coordinate of the target.
     * @param targetX the X coordinate of the target.
     * @param golfBall the GolfBall object.
     * @return the angle in degrees.
     */
    public double calculateAngleDegree(double[] direction, double targetY, double targetX, GolfBall golfBall) {
        double angle;
        if (direction[0] != 0) {
            angle = Math.atan(direction[1] / direction[0]);
            angle = Math.toDegrees(angle);
        } else {
            angle = 90;
        }
        if (targetX < golfBall.getX() && targetY > golfBall.getY()) {
            angle = 180 - angle;
        }
        if (targetX < golfBall.getX() && targetY < golfBall.getY()) {
            angle = 180 + angle;
        }
        if (targetX > golfBall.getX() && targetY < golfBall.getY()) {
            angle = 360 - angle;
        }
        return angle;
    }

    /**
     * Converts an angle in degrees to radians.
     *
     * @param angle the angle in degrees.
     * @return the angle in radians.
     */
    public double calculateAngleRadian(double angle) {
        return (angle * Math.PI) / 180;
    }

    /**
     * Checks the minimum distance from the calculated path to the hole.
     *
     * @param coordinatesPath the calculated path.
     * @return the minimum distance to the hole.
     */
    public double checkDistanceFromHole(CoordinatesPath coordinatesPath) {
        double[][] path = coordinatesPath.getPath();
        double holeX = this.hole.getX();
        double holeY = this.hole.getY();
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path[0].length; i++) {
            double ballX = path[0][i];
            double ballY = path[1][i];

            double distanceSquared = (ballX - holeX) * (ballX - holeX) + (ballY - holeY) * (ballY - holeY);
            minDistanceSquared = Math.min(minDistanceSquared, distanceSquared);
        }
        return Math.sqrt(minDistanceSquared);
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
        return "BasicBot";
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
