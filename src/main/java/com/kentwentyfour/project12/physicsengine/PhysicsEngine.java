package com.kentwentyfour.project12.physicsengine;


import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.movableobjects.ReboundingObstacle;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.AreaType;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.ObstacleArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Grass;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Sand;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.mathpackage.FormulaCalculator;
import com.kentwentyfour.project12.mathpackage.ODESolver;
import com.kentwentyfour.project12.mathpackage.PartialDerivative;
import com.kentwentyfour.project12.ReferenceStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;

public class PhysicsEngine {

    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private double grav_constant = 9.81;
    private double pi = Math.PI;
    public MapManager mapManager;


    // main solver
    ODESolver solver = new ODESolver();
    //  calculator for CourseProfileFormula h(x,y)
    FormulaCalculator calcCPF = new FormulaCalculator();
    ArrayList<String> CPF_parsed = new ArrayList<>();
    public PartialDerivative height_PartialDerivative = new PartialDerivative("h");


    public PhysicsEngine(){
        //  preparation of  CourseProfileFormula for calculations
        this.mapManager = referenceStore.getMapManager();
        String CourseProfileFormula = referenceStore.getCourseProfileFormula();
        this.CPF_parsed =new ArrayList<>(calcCPF.parseString(CourseProfileFormula));
        height_PartialDerivative.setEquation(CourseProfileFormula);
        height_PartialDerivative.addVariables("x","y");
        ODESolver.addPartialDerivative(height_PartialDerivative);
    }

    /**
     * computes the force normal to  terrarian / parallel to friction force, based on applied force and angle
     * @param angle
     * @param force
     * @return double[2] containing: [0]-Fx  and  [1]-Fy
     * @author Kordian
     */
    public double[] computeAppliedForceDistribution(double angle, double force){
        double[] X_Y_forces = new double[2];
        //calculating force towards X axis
        X_Y_forces[0] = force*Math.cos(angle);
        X_Y_forces[1] = force*Math.sin(angle);
        return X_Y_forces;
    }
    /**
     * computes h(x,y) formula for given coordinates
     * @param x
     * @param y
     * @return z coordinate ( height )
     * @author Kordian
     */
    public double computeHeight(double x, double y){
        calcCPF.setIndependendValue("x",x);
        calcCPF.setIndependendValue("y",y);
        return calcCPF.calculateRPN(CPF_parsed);
    }

    /**
     * Calculates the path of the golf ball
     *
     * @return double[Number of coordinates][2]
     */
    public CoordinatesPath calculateCoordinatePath(GolfBall golfBall, double velocityX, double velocityY) {
        return calculateCoordinatePath(golfBall, velocityX, velocityY, 0.01,1); // Default step size
    }
    public CoordinatesPath calculateCoordinatePath(GolfBall golfBall, double velocityX, double velocityY, double customStepSize,int customInitialTime){
        //velocityX =  1.8767497876519854;
        //velocityY = 1.0509900368707568;
        double x_coordinate =  golfBall.getX();
        double y_coordinate =  golfBall.getY();

        // getting frictions for current position of ball
        MatrixMapArea mapObj =  mapManager.accessObject(x_coordinate,y_coordinate);
        double current_kf =0.1;
        if(mapObj instanceof AreaType){
            current_kf = ((AreaType) mapObj).getKineticFriction();
        }else{
            System.err.println("The starting position of ball is not playable area");
            System.err.println("X: "+x_coordinate+"Y: "+y_coordinate);
            System.err.println("AreaType: "+"Grass: "+(mapObj instanceof Grass)+" Water: "+(mapObj instanceof Water)+" Sand: "+(mapObj instanceof Sand));
        }

        // setting up formulas and ODE solver
        String[] equations = new String[] {
                "-9.81 * dh/dx / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vx / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )", // dvx/dt
                "-9.81 * dh/dy / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vy / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )",  // dvy/dt
                "vx",
                "vy"
        };
        List<String> variables = Arrays.asList( "t","vx","vy","x", "y","k_f");
        double[] in_Conditions = {0,velocityX, velocityY,x_coordinate,y_coordinate,current_kf};

        // calculating new coordinates until any stopping condition is met
        String stoppingCondition="";
        LinkedList<Double> path_coordinates_X = new LinkedList<>();
        LinkedList<Double> path_coordinates_Y = new LinkedList<>();

        while (stoppingCondition.isEmpty()){
            //System.err.println(in_Conditions[3]+" , "+  in_Conditions[4]);
            Double[][] results = ODESolver.rungeKutta(equations, customStepSize, customInitialTime, in_Conditions, variables);
            double  x_velocity =  results[1][0];
            double  y_velocity =  results[2][0];
            for(int i=0;i<results[0].length;i++ ){

                double y_coordinate_change=1; // values must be over 0.01
                double x_coordinate_change=1;
                double x_velocity_change=1;
                double y_velocity_change=1;

                //updating velocities and coordinates
                x_coordinate = results[3][i];
                y_coordinate = results[4][i];



                // checking for stopping condition
                if(i>1){
                    x_velocity_change =abs( abs(results[1][i])-abs(x_velocity));
                    y_velocity_change =abs( abs(results[2][i])-abs(y_velocity));
                    x_velocity = results[1][i];
                    y_velocity= results[2][i];
                    x_coordinate_change = abs(results[3][i]-results[3][i-1]);
                    y_coordinate_change = abs(results[4][i]-results[4][i-1]);
                }
                stoppingCondition = checkStoppingConditions(golfBall,x_velocity_change,y_velocity_change,x_velocity,y_velocity, x_coordinate, y_coordinate,x_coordinate_change,y_coordinate_change);
                if(!stoppingCondition.isEmpty()){
                    // saves coordinates to final path ( includes coordinates where ball hit the obstacle )
                    path_coordinates_X.add(x_coordinate);
                    path_coordinates_Y.add(y_coordinate);
                    break;
                }

                // updating frictions for new position if the ball is on AreaType area
                mapObj =  mapManager.accessObject(x_coordinate,y_coordinate);
                if(mapObj instanceof AreaType ){
                    double new_kf = ((AreaType) mapObj).getKineticFriction();
                    if(current_kf!=new_kf){
                        // repeat calculations for new friction
                        current_kf = new_kf;
                        path_coordinates_X.add(x_coordinate);
                        path_coordinates_Y.add(y_coordinate);
                        break;
                    }
                }
                MovableObjects obstacle = mapManager.checkForCollisionWithObstacle(golfBall, x_coordinate, y_coordinate);
                if (obstacle != null && obstacle instanceof ReboundingObstacle) {

                    double restitutionCoefficient = ((ReboundingObstacle) obstacle).getRestitutionCoefficient();

                    // Calculate collision normal vector
                    double normalX = x_coordinate - obstacle.getX();
                    double normalY = y_coordinate - obstacle.getY();

                    // Calculate magnitude of normal vector
                    double normalMagnitude = Math.sqrt(normalX * normalX + normalY * normalY);

                    // Normalize the normal vector
                    double normalUnitX = normalX / normalMagnitude;
                    double normalUnitY = normalY / normalMagnitude;

                    // Calculate dot product of velocity and normal vector
                    double dotProduct = x_velocity * normalUnitX + y_velocity * normalUnitY;

                    // Reflect velocities based on the collision
                    x_velocity = x_velocity - 2 * dotProduct * normalUnitX * restitutionCoefficient;
                    y_velocity = y_velocity - 2 * dotProduct * normalUnitY * restitutionCoefficient;



                    // Update coordinates based on previous state
                    if (i > 0) {
                        x_coordinate = results[3][i - 1];
                        y_coordinate = results[4][i - 1];
                    } else {
                        x_coordinate = results[3][i];
                        y_coordinate = results[4][i];
                    }
                    // calculates new path with new initial conditions
                    break;
                }

                // saves coordinates to final path ( includes coordinates where ball hit the obstacle )
                path_coordinates_X.add(x_coordinate);
                path_coordinates_Y.add(y_coordinate);

                if(!stoppingCondition.isEmpty()){
                    break;
                }


            }
            // updating initial condition for next while loop
            in_Conditions = new double[]{0,x_velocity, y_velocity, x_coordinate, y_coordinate, current_kf};

        }


        // Returning the list of coordinates
        double[][] finalPath = new double[2][path_coordinates_X.size()];
        for (int i = 0; i < path_coordinates_X.size(); i++) {
            finalPath[0][i] = path_coordinates_X.get(i);
            finalPath[1][i] = path_coordinates_Y.get(i);
        }
        // stepSize is timeInterval as it is used as  chang of time in ODE solver
        return new CoordinatesPath(finalPath,customStepSize,stoppingCondition);
    }



    /**
     * Checks if moving object:
     * - hits an obstacle
     * - stops moving due to friction
     */
    private String checkStoppingConditions( GolfBall golfBall, double x_velocity_change, double y_velocity_change, double x_velocity_last, double y_velocity_last, double x_coordinate, double y_coordinate,double x_coordinate_change,double y_coordinate_change){
        double dh_dx = height_PartialDerivative.calculatePD_notation("dh/dx",x_coordinate,y_coordinate);
        double dh_dy = height_PartialDerivative.calculatePD_notation("dh/dy",x_coordinate,y_coordinate);

        MatrixMapArea obj = mapManager.accessObject(x_coordinate,y_coordinate);
        if(ballInHole( x_velocity_last, y_velocity_last,  x_coordinate,  y_coordinate)){
            return "ball_in_the_hole";
        }
        switch (obj) {
            case null -> {
                return "outside_of_playable_area";
            }
            case ObstacleArea obstacleArea -> {
                return "obstacle_hit";
            }
            case AreaType areaType -> {
                double formula_value = Math.sqrt(dh_dx * dh_dx + dh_dy * dh_dy)+1;
                if (areaType.getStaticFriction() > formula_value)  {
                    return "static_friction_overcomes_the_force";
                }else if(formula_value !=1&& x_coordinate_change<0.0001 && y_coordinate_change<0.0001){ // not flat surface
                    return "static_friction_overcomes_the_force";
                }
                else if(formula_value ==1&&x_velocity_change<0.001 && y_velocity_change<0.001){ // flat surface
                    return "static_friction_overcomes_the_force";
                }
            }
            default -> {
                return "";
            }
        }
        return "";
    }


    /**
     * Checks midpoint of ball is in hole area
     * @param x_velocity_last
     * @param y_velocity_last
     * @param x_coordinate
     * @param y_coordinate
     * @return
     */
    public boolean ballInHole(double x_velocity_last,double y_velocity_last, double x_coordinate, double y_coordinate){
        Hole hole = referenceStore.getHole();
        double radiusOfHole = hole.getRadius();
        double x_hole = hole.getX();
        double y_hole = hole.getY();
        double distance = Math.sqrt(Math.pow(x_coordinate - x_hole, 2) + Math.pow(y_coordinate - y_hole, 2));
        //System.out.println(distance +" "+x_velocity_last+ " "+y_velocity_last);
        // Ball in the hole
        return distance < radiusOfHole && x_velocity_last < 4 && y_velocity_last < 4;
    }
    public static void main(String[] args){
        //setting up golf ball/s

        String formula = "sin( ( x - y ) / 7 ) + 0.5 ";


        // Get reference store
        ReferenceStore referenceStore = ReferenceStore.getInstance();

        //store given formula
        referenceStore.setCourseProfileFormula(formula);

        //create and store MapManager
        MapManager  mapManager = new MapManager();
        referenceStore.setMapManagerReference(mapManager);

        //create and store golf balls
        ArrayList<GolfBall> balls =  new ArrayList<GolfBall>();
        balls.add(new GolfBall(1,1,.1,0.1));

        Hole hole = new Hole(2,2,0.15);
        referenceStore.setHoleReference(hole);

        PhysicsEngine engine =  new PhysicsEngine();
        for(int d=0; d<10;d++){
            CoordinatesPath results = engine.calculateCoordinatePath(balls.get(0),2,2);
            double[][] path = results.getPath();

            double holeX = hole.getX();
            double holeY = hole.getY();
            double minDistanceSquared = Double.POSITIVE_INFINITY;

            for (int i = 0; i < path[0].length; i++) {
                double ballX = path[0][i];
                double ballY = path[1][i];

                double distanceSquared = (ballX - holeX) * (ballX - holeX) + (ballY - holeY) * (ballY - holeY);
                minDistanceSquared = Math.min(minDistanceSquared, distanceSquared);
            }
            System.out.println( Math.sqrt(minDistanceSquared));
        }





       //// System.out.println("_____X__");
       //// System.out.println(Arrays.toString(path[0]));
       //// System.out.println("_____Y__");
       //// System.out.println(Arrays.toString(path[1]));
       //// System.out.println("___Stopping_Condition___");
       // System.out.println(results.getStoppingCondition());

    }
}

