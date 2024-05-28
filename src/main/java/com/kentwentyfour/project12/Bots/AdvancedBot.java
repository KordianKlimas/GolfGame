package com.kentwentyfour.project12.Bots;

import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;

public class AdvancedBot implements BotPlayer {
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();

    public AdvancedBot() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
    }

    public CoordinatesPath calculatePath(GolfBall golfBall) {
        Hole hole = referenceStore.getHole();
        CoordinatesPath path = null;
        double targetX = hole.getX();
        double targetY = hole.getY();
        double[] direction = calculateDirection(targetX, targetY, golfBall);
        double angleDegrees = calculateAngleDegree(direction, targetY, targetX, golfBall);
        double angleRadians = calculateAngleRadian(angleDegrees);

        // Ensure terrain data is generated

        double[] velocities = calculateVelocities(angleRadians, direction, targetX, targetY, golfBall);
        double velocityX = velocities[0];
        double velocityY = velocities[1];

        CoordinatesPath coordinatesPath = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
         path = coordinatesPath;
        return path;
    }

    public double[] calculateDirection(double targetx, double targety, GolfBall golfBall) {
        double distancex = Math.abs(targetx - golfBall.getX());
        double distancey = Math.abs(targety - golfBall.getY());
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

    public double[] calculateVelocities(double angleRad, double[] directions, double targetx, double targety, GolfBall golfBall) {
        double dx = Math.abs(targetx - golfBall.getX());
        double dy = Math.abs(targety - golfBall.getY());
        double d = Math.sqrt(dx * dx + dy * dy);
        double scaleFactor = chooseScaleFactor(targetx, targety, golfBall);
        double slopeFactor = calculateSlopeFactor(golfBall.getX(), golfBall.getY());
        double v = Math.sqrt(directions[0] * directions[0] + directions[1] * directions[1]);
        v = v * scaleFactor * slopeFactor;
        double vx = v * Math.cos(angleRad);
        double vy = v * Math.sin(angleRad);
        double[] velocities = {vx, vy, scaleFactor};
        return velocities;
    }

    public double chooseScaleFactor(double targetx, double targety, GolfBall golfBall) {
        double dx = Math.abs(targetx - golfBall.getX());
        double dy = Math.abs(targety - golfBall.getY());
        double d = Math.sqrt(dx * dx + dy * dy);
        return 0.25 * d;
    }

    private double calculateSlopeFactor(double x, double y) {
        double dhdx = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dx", x, y);
        double dhdy = physicsEngine.height_PartialDerivative.calculatePD_notation("dh/dy", x, y);
        double slopeMagnitude = Math.sqrt(dhdx * dhdx + dhdy * dhdy);
        return 1.0 - slopeMagnitude;
    }

    public double calculateAngleDegree(double[] direction, double targety, double targetx, GolfBall golfBall) {
        double angle;
        if (direction[0] != 0) {
            angle = Math.atan(direction[1] / direction[0]);
            angle = Math.toDegrees(angle);
        } else {
            angle = 90;
        }
        if (targetx < golfBall.getX() && targety > golfBall.getY()) {
            angle = 180 - angle;
        }
        if (targetx < golfBall.getX() && targety < golfBall.getY()) {
            angle = 180 + angle;
        }
        if (targetx > golfBall.getX() && targety < golfBall.getY()) {
            angle = 360 - angle;
        }
        return angle;
    }

    public double calculateAngleRadian(double angle) {
        return (angle * Math.PI) / 180;
    }

    public static void main(String args[]) {
        GolfBall ball = new GolfBall(0.0, 0.0, 0.0459, 0.15);
        ArrayList<GolfBall> golfBalls = new ArrayList<>();
        golfBalls.add(ball);
        ReferenceStore referenceStore = ReferenceStore.getInstance();
        Hole hole = new Hole(1,1,0.15);
        referenceStore.setHoleReference(hole);
        AdvancedBot advancedBot = new AdvancedBot();

    }
}
