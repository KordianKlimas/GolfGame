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
    private long computationTime;
    private int numberOfTurns = 1;


    public BasicBot() {}

    public CoordinatesPath calculatePath(GolfBall golfBall,double targetX, double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path = null;
        //System.err.println(targetX + " " + targetY);
        //this.targetX = targetX;
        //this.targetY = targetY;
        double pointX = 2.0;     // X coordinate of the node
        double pointY = -2.0;    // Y coordinate of the node

        double velocityX = pointX - golfBall.getX();
        double velocityY = pointY - golfBall.getY();

        double buffX = velocityX;
        double buffY = velocityY;
        double change = 0.25;          // Velocities are changed by substracting this value;
        // It affects the running time;
        // If you want the code to run faster, increase the value;
        // the value of this variable affects the accuracy of the bot;
        for(int i = 0; i <= (int)(Math.abs(buffX) / change); i++){
            if(buffX > 0){
                velocityX = buffX - (change * i);
            }
            if(buffX < 0){
                velocityX = buffX + (change * i);
            }
            for(int j = 0; j <= (int)(Math.abs(buffY) / change); j++){
                if(buffY > 0){
                    velocityY = buffY - (change * j);
                }
                if(buffY < 0){
                    velocityY = buffY + (change * j);
                }
                path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY);
                double[][] coordinates = path.getPath();
                double lastX = Math.round(coordinates[0][coordinates[0].length - 1]);
                double lastY = Math.round(coordinates[1][coordinates[0].length - 1]);
                if(lastX == pointX && lastY == pointY){
                    j = (int)(Math.abs(buffY) / change) + 1;
                    i = (int)(Math.abs(buffX) / change) + 1;
                }
            }
        }

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;
        double[][] arrOfCoordinates =path.getPath();
        System.err.println(Arrays.deepToString(arrOfCoordinates));
        System.err.println("expected last coordinates: " +pointX +" " + pointY);
        System.err.println("last coordinates: "+arrOfCoordinates[0][arrOfCoordinates[0].length-1] +" "+ arrOfCoordinates[1][arrOfCoordinates[1].length-1]);
        return path;
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
