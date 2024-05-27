package com.kentwentyfour.project12.Bot;


import com.kentwentyfour.project12.GameObjects.GolfBall;
import com.kentwentyfour.project12.GameObjects.MapManager;
import com.kentwentyfour.project12.GameObjects.MappableObject;
import com.kentwentyfour.project12.PhysicsEnginePackage.CoordinatesPath;
import com.kentwentyfour.project12.PhysicsEnginePackage.PhysicsEngine;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicBot{
    private PhysicsEngine engine;

    public BasicBot(String CourseProfileFormula, ArrayList<GolfBall> golf_balls){
        
    }
    public Double[][] newCoordinates(GolfBall golfBall, double velocity, double targetX, double targetY){
        double velocityX = velocity * Math.cos(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        double velocityY = velocity * Math.sin(Math.toRadians(findAngle(golfBall.getX(), golfBall.getY(), targetX, targetY)));
        CoordinatesPath coordinatesPath = engine.calculateCoordinatePath(golfBall,velocityX, velocityY);
        double[][] path = coordinatesPath.getPath();
        Double[][]res = new Double[path.length][path[0].length];
        for (int i = 0;i<path.length;i++){
            for(int j=0;j<path[i].length;j++){
                res[i][j] = path[i][j];
            }
        }
        return res;
    }
    public double findAngle(double x1, double y1, double x2, double y2) {
        double delta_x = x2 - x1;
        double delta_y = y2 - y1;
        double angle_rad = Math.atan2(delta_y, delta_x);
        double angle_deg = Math.toDegrees(angle_rad);
        return angle_deg;
    }
    public static void main(String args[]){
        GolfBall boll = new GolfBall(0.0,0.0,0.0459,0.15);
        ArrayList<GolfBall> golf = new ArrayList<>();
        golf.add(boll);
        BasicBot bot = new BasicBot("sin( ( x - y ) / 7 ) + 0.5", golf);
        double targetX = 5;
        double targetY = 7;
        double velocity = 5;
        Double[][] newCoordinates = bot.newCoordinates(boll, velocity, targetX, targetY);
        System.out.println("--------X----------");
        System.out.println(Arrays.toString(newCoordinates[0]));
        System.out.println(" ");
        System.out.println("--------Y----------");
        System.out.print(Arrays.toString(newCoordinates[1]));
        System.out.println(" ");
    }
}
