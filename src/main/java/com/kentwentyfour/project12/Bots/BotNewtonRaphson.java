package com.kentwentyfour.project12.Bots;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;

import java.util.Arrays;

public class BotNewtonRaphson implements BotPlayer {
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private Hole hole;
    public GolfBall golf_ball;
    double customStepSize = 0.1;
    int customTime =1;

    public BotNewtonRaphson() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.hole = referenceStore.getHole();
    }


    /**
     * Calculates best CoordinatesPath
     * @return CoordinatesPath
     */
    public CoordinatesPath calculatePath(GolfBall golf_ball) {
        this.golf_ball=golf_ball;
        double Vx = 2;
        double Vy = 2;
        double learningRate = 0.1;
        int maxIter = 30;
        double velocity_change =1;
        CoordinatesPath path = null;
        for (int iter = 0; iter < maxIter; iter++) {
            learningRate-=0.02;
            physicsEngine.customStepSize = this.customStepSize;
            physicsEngine.customTime= this.customTime;
            path = physicsEngine.calculateCoordinatePath(golf_ball, Vx, Vy);
            if ("ball_in_the_hole".equals(path.getStoppingCondition())) {
                System.out.println("Converged to the hole with velocities: vx = " + Vx + ", vy = " + Vy);
                return path;
            }
            double[] gradient = computeGradient(Vx, Vy, velocity_change);
            Vx -= learningRate * gradient[0];
            Vy -= learningRate * gradient[1];

            // Enforcing velocity constraints
            Vx = Math.max(-5, Math.min(5, Vx));
            Vy = Math.max(-5, Math.min(5, Vy));

            System.out.println("bot Vx: " +Vx+ "bot Vy: " + Vy);
        }
        System.err.println("Did not converge within the maximum number of iterations.");
        return path;
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
    public double[] computeGradient(double Vx, double Vy, double velocity_change) {
        // Perturb Vx
        double distancePlusVx = checkDistanceFromHole(physicsEngine.calculateCoordinatePath(golf_ball, Vx + velocity_change, Vy));
        double distanceMinusVx = checkDistanceFromHole(physicsEngine.calculateCoordinatePath(golf_ball, Vx - velocity_change, Vy));
        double gradientVx = (distancePlusVx - distanceMinusVx) / (2 * velocity_change);

        // Perturb Vy
        double distancePlusVy = checkDistanceFromHole(physicsEngine.calculateCoordinatePath(golf_ball, Vx, Vy + velocity_change));
        double distanceMinusVy = checkDistanceFromHole(physicsEngine.calculateCoordinatePath(golf_ball, Vx, Vy - velocity_change));
        double gradientVy = (distancePlusVy - distanceMinusVy) / (2 * velocity_change);

        return new double[]{gradientVx, gradientVy};
    }
    public static void main(String[] args){
        ReferenceStore referenceStore = ReferenceStore.getInstance();
        String formula = "0.4 * ( 0.9 -  2.718 ^ ( (  x ^ 2 + y ^ 2 ) / -8 ) )";
        formula= "sin( ( x - y ) / 7 ) + 0.5";
        //formula ="1";
        referenceStore.setCourseProfileFormula(formula);
        //Set frictions
        referenceStore.setFrictionsAreaType("Grass",0.05,0.1 );
        referenceStore.setFrictionsAreaType("Sand",0.1,0.2 );

        // Initialize mapManager and map
        MapManager mapManager = new MapManager();
        mapManager.generateTerrainData();

        // Add movable objects to the map
        Hole hole = new Hole(2,2,0.15);
        GolfBall golf_ball =new GolfBall(-2,-2,0.15,0.1);
        referenceStore.setHoleReference(hole);
        mapManager.generateTerrainData();


        referenceStore.setMapManagerReference(mapManager);
        referenceStore.setPhysicsEngine(new PhysicsEngine());

        BotNewtonRaphson botNewtonRaphson = new BotNewtonRaphson();
        CoordinatesPath path =   botNewtonRaphson.calculatePath(golf_ball);
        double[][] velocities = path.getPath();
        //System.out.println("final: Vx "+ Arrays.toString(velocities[0]) + " Vy "+ Arrays.toString(velocities[1]));
    }
}

