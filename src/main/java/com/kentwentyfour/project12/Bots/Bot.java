package com.kentwentyfour.project12.Bots;

import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;

public class Bot {
    private MapManager mapGenerator;
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();

    public Bot(GolfBall golf_ball) {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapGenerator = referenceStore.getMapManager();
    }

    public Double[][] newCoordinates(GolfBall golfBall, double targetX, double targetY) {
        double[] direction = calculateDirection(targetX, targetY, golfBall);
        double angleDegrees = calculateAngleDegree(direction, targetY, targetX, golfBall);
        double angleRadians = calculateAngleRadian(angleDegrees);

        // Ensure terrain data is generated

        double[] velocities = calculateVelocities(angleRadians, direction, targetX, targetY, golfBall);
        double velocityX = velocities[0];
        double velocityY = velocities[1];

        CoordinatesPath coordinatesPath = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
        double[][] path = coordinatesPath.getPath();
        Double[][] res = new Double[path.length][path[0].length];
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                res[i][j] = path[i][j];
            }
        }
        return res;
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

        Bot bot = new Bot(ball);

        double targetX = 5;
        double targetY = 7;

       Double[][] newCoordinates = bot.newCoordinates(ball, targetX, targetY);
        System.out.println("--------X----------");
        System.out.println(Arrays.toString(newCoordinates[0]));
        System.out.println(" ");
        System.out.println("--------Y----------");
        System.out.print(Arrays.toString(newCoordinates[1]));
        System.out.println(" ");
    }
}
