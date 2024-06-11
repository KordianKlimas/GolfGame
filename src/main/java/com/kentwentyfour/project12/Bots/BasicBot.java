package com.kentwentyfour.project12.Bots;


import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicBot implements BotPlayer{

    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private PhysicsEngine  physicsEngine = referenceStore.getPhysicsEngine();
    public BasicBot(){
    }
    public Double[][] newCoordinates(GolfBall golfBall, double velocity, double targetX, double targetY){
        double velocityX = velocity * Math.cos(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        double velocityY = velocity * Math.sin(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        CoordinatesPath coordinatesPath = physicsEngine.calculateCoordinatePath(golfBall,velocityX, velocityY);
        double[][] path = coordinatesPath.getPath();
        Double[][]res = new Double[path.length][path[0].length];
        for (int i = 0;i<path.length;i++){
            for(int j=0;j<path[i].length;j++){
                res[i][j] = path[i][j];
            }
        }
        return res;
    }
    public CoordinatesPath calculatePath(GolfBall golfBall) {
        double targetX =  referenceStore.getHole().getX();
        double targetY =  referenceStore.getHole().getY();
        double velocityX = 5 * Math.cos(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        double velocityY = 5 * Math.sin(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        return physicsEngine.calculateCoordinatePath(golfBall,velocityX, velocityY);
    }
    public double findAngle(double x1, double y1, double x2, double y2) {
        double delta_x = x2 - x1;
        double delta_y = y2 - y1;
        double angle_rad = Math.atan2(delta_y, delta_x);
        double angle_deg = Math.toDegrees(angle_rad);
        return angle_deg;
    }
}
