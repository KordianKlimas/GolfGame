package com.kentwentyfour.project12.bots.improvedbot;


import com.kentwentyfour.project12.bots.BasicBot;
import com.kentwentyfour.project12.bots.BotPlayer;
import com.kentwentyfour.project12.bots.MultipleTurnBot;
import com.kentwentyfour.project12.bots.improvedbot.resources.AStarAlgorithm;
import com.kentwentyfour.project12.bots.improvedbot.resources.Waypoint;
import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MazeBot implements BotPlayer, MultipleTurnBot {
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private double  targetX;
    private double targetY;
    private GolfBall ball;
    private List<Waypoint> aStarPath;
    private int count = 0;

    private long computationTime;
    private int numberOfTurns = 1;

    public MazeBot() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapManager = referenceStore.getMapManager();
        ArrayList<GolfBall> golfBallArrayList = referenceStore.getGolfballList();
        this.ball = golfBallArrayList.getFirst() ;
    }
    @Override
    public CoordinatesPath calculatePath(GolfBall golfBall,double targetX,double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path = null;
        //this.targetX = targetX;
        //this.targetY = targetY;

        double velocityX = targetX - golfBall.getX();
        double velocityY = targetY - golfBall.getY();

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
                if(lastX == targetX && lastY == targetY){
                    j = (int)(Math.abs(buffY) / change) + 1;
                    i = (int)(Math.abs(buffX) / change) + 1;
                }
            }
        }

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;
        double[][] arrOfCoordinates =path.getPath();
        System.err.println(Arrays.deepToString(arrOfCoordinates));
        System.err.println("expected last coordinates: " + targetX +" " + targetY);
        System.err.println("last coordinates: "+arrOfCoordinates[0][arrOfCoordinates[0].length-1] +" "+ arrOfCoordinates[1][arrOfCoordinates[1].length-1]);
        return path;
    }

    public void genereteWaypointPath(double targetX, double targetY){
        // decrease range for more midpoints 1=max 100=min
        this.targetX = targetX;
        this.targetY = targetY;
        AStarAlgorithm astarAlgorithm = new AStarAlgorithm();
        this.aStarPath = astarAlgorithm.generateWaypoints(ball.getX(), ball.getY(), this.targetX, this.targetY);
        if(this.aStarPath == null || this.aStarPath.isEmpty()){
            System.err.println("No waypoints created");
        }
        for (Waypoint waypoint : aStarPath) {
            System.out.println("Waypoint: " + waypoint);
        }
        mapManager.drawAStarPath(aStarPath);
    }
    public List<Waypoint> getCurrentWaypointPath(){
        return this.aStarPath;
    }


    public double checkDistanceFromHole(CoordinatesPath coordinatesPath) {
        double[][] path = coordinatesPath.getPath();

        Waypoint targetWaypoint = aStarPath.get(count);
        double holeX = targetWaypoint.x;
        double holeY = targetWaypoint.y;

        System.out.println("Checking path for " + holeX + " " + holeY);

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
