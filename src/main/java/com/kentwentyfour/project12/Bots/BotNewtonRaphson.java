package com.kentwentyfour.project12.Bots;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;

public class BotNewtonRaphson {
    private PhysicsEngine physicsEngine;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private Hole hole;
    public GolfBall golf_ball;
    double customStepSize = 0.1;
    int customTime =2;

    public BotNewtonRaphson(GolfBall golf_ball) {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.hole = referenceStore.getHole();
        this.golf_ball=golf_ball;
    }


    /**
     * Calculates the initial velocities
     * @return
     */
    public double[] calculateInitialVelocities() {
        double Vx = 0;
        double Vy = 0;
        double learningRate = .1;
        int maxIter = 100;
        double velocity_change =1;




        for (int iter = 0; iter < maxIter; iter++) {
            physicsEngine.customStepSize = this.customStepSize;
            physicsEngine.customTime= this.customTime;
            CoordinatesPath path = physicsEngine.calculateCoordinatePath(golf_ball, Vx, Vy);

            if ("ball_in_the_hole".equals(path.getStoppingCondition())) {
                System.out.println("Converged to the hole with velocities: vx = " + Vx + ", vy = " + Vy);
                return new double[]{Vx, Vy};
            }

            double[] gradient = computeGradient(Vx, Vy, velocity_change);
            Vx -= learningRate * gradient[0];
            Vy -= learningRate * gradient[1];

            // Enforce velocity constraints
            Vx = Math.max(-5, Math.min(5, Vx));
            Vy = Math.max(-5, Math.min(5, Vy));

            System.err.println(Vx+ " " + Vy);
        }

        //System.out.println("Did not converge within the maximum number of iterations.");
        return new double[]{Vx, Vy};
    }


    public  double checkDistanceFromHole(CoordinatesPath coordinatesPath){
        double[][] path=coordinatesPath.getPath();

        double holeX =this.hole.getX();
        double holeY =this.hole.getY();
        double dist = 100;
       for(int i = 0; i<path[0].length;i++){
           double ballX = path[0][i];
           double ballY = path[1][i];
           if(dist > Math.sqrt((ballX - holeX) * (ballX - holeX) + (ballY - holeY) * (ballY - holeY))){
               dist =  Math.sqrt((ballX - holeX) * (ballX - holeX) + (ballY - holeY) * (ballY - holeY));
           }
       }
        return dist ;

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
        referenceStore.setCourseProfileFormula("sin( ( x - y ) / 7 ) + 0.5");
        //Set frictions
        referenceStore.setFrictionsAreaType("Grass",0.05,0.1 );
        referenceStore.setFrictionsAreaType("Sand",0.1,0.2 );

        // Initialize mapManager and map
        MapManager mapManager = new MapManager();
        mapManager.generateTerrainData();

        // Add movable objects to the map
        Hole hole = new Hole(3,3,0.15);
        GolfBall golf_ball =new GolfBall(0,0,0.15,0.1);
        referenceStore.setHoleReference(hole);
        mapManager.generateTerrainData();


        referenceStore.setMapManagerReference(mapManager);
        referenceStore.setPhysicsEngine(new PhysicsEngine());

        BotNewtonRaphson botNewtonRaphson = new BotNewtonRaphson(golf_ball);
        double[] velocities =   botNewtonRaphson.calculateInitialVelocities();
        System.out.println("final: Vx "+velocities[0]+ " Vy "+velocities[1]);
    }
}

