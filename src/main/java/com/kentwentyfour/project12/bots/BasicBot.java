package com.kentwentyfour.project12.bots;

import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;


public class BasicBot implements BotPlayer {
    private static final double thresholdDistance = 0.1;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private PhysicsEngine physicsEngine = referenceStore.getPhysicsEngine();
    private Hole hole = referenceStore.getHole();

    private long computationTime;
    private int numberOfTurns = 1;

    private double targetX;
    private double targetY;



    public BasicBot() {}

    public CoordinatesPath calculatePath(GolfBall golfBall,double targetX, double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path = null;
        System.err.println(targetX + " " + targetY);
        this.targetX = targetX;
        this.targetY = targetY;

        double[] direction = calculateDirection(golfBall,targetX,targetY);
        double angleDegrees = calculateAngleDegree(direction, targetY, targetX, golfBall);
        double angleRadians = calculateAngleRadian(angleDegrees);

        // Ensure terrain data is generated

        double[] velocities = calculateVelocities(angleRadians, direction, golfBall);
        double velocityX = velocities[0];
        double velocityY = velocities[1];

        path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
        double distance= checkDistanceFromHole(path);
        if (distance > thresholdDistance) {
            velocityX *= 10;
            velocityY *= 10;
            path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
        }
        //System.err.println(path[0][path[0].size()-1] + " " + targetY);

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;
        System.err.println(Arrays.deepToString(path.getPath()));
        return path;
        //    return physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
    }

    public double[] calculateDirection( GolfBall golfBall,double targetX, double targetY) {

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
    private double calculateSlopeFactor(double x, double y) {
        double dhdx = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dx", x, y);
        double dhdy = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dy", x, y);
        double slopeMagnitude = Math.sqrt(dhdx * dhdx + dhdy * dhdy);
        return 1.0 - slopeMagnitude;
    }

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

    public double calculateAngleRadian(double angle) {
        return (angle * Math.PI) / 180;
    }

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
