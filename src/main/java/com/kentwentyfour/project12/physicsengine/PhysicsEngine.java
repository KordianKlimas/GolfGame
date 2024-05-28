package com.kentwentyfour.project12.physicsengine;


import com.kentwentyfour.project12.gameobjects.*;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areaobstacles.Water;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.AreaType;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areaobstacles.ObstacleType;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Grass;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Sand;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
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

    public boolean customTimeAndStepSize = false;
    public double customStepSize = 0;
    public int customTime = 0;

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
    public CoordinatesPath calculateCoordinatePath(GolfBall golfBall, double velocityX, double velocityY){

        double x_coordinate =  golfBall.getX();
        double y_coordinate =  golfBall.getY();
        //System.out.println("Starting ball coords: "+ x_coordinate+" " + y_coordinate);

        // getting frictions for current position of ball
        MatrixMapArea area =  mapManager.accessObject(x_coordinate,y_coordinate);
        double current_kf =0.1;
        if(area instanceof AreaType){
            current_kf = ((AreaType) area).getKineticFriction();
        }else{
            System.err.println("The starting position of ball is not playable area");
            System.err.println("X: "+x_coordinate+"Y: "+y_coordinate);
            System.err.println("AreaType: "+"Grass: "+(area instanceof Grass)+"Water: "+(area instanceof Water)+"Sand: "+(area instanceof Sand));
        }

        // setting up formulas and ODE solver
        String[] equations = new String[] {
                "-9.81 * dh/dx / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vx / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )", // dvx/dt
                "-9.81 * dh/dy / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vy / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )",  // dvy/dt
                "vx",
                "vy"
        };
        List<String> variables = Arrays.asList( "t","vx","vy","x", "y","k_f");
        double stepSize = 0.01;
        int in_Time = 2;
        if (customTimeAndStepSize){
            System.out.println("Changed");
             stepSize = customStepSize;
             in_Time = customTime;
        }
        double[] in_Conditions = {0,velocityX, velocityY,x_coordinate,y_coordinate,current_kf};

        // calculating new coordinates until any stopping condition is met
        LinkedList<Double> path_coordinates_X = new LinkedList<>();
        LinkedList<Double> path_coordinates_Y = new LinkedList<>();

        String stoppingCondition="";
        while (stoppingCondition.isEmpty()){
            Double[][] results = ODESolver.rungeKutta(equations, stepSize, in_Time, in_Conditions, variables);
            //System.out.println(Arrays.toString(results[4]));
            //System.out.println(Arrays.toString(results[4]));
            double  x_velocity =  results[1][0];
            double  y_velocity =  results[2][0];
            //System.out.println(Arrays.toString(results[3]));
            //System.out.println(Arrays.toString(results[4]));
            for(int i=0;i<results[0].length;i++ ){

                double y_coordinate_change=1; // values must be over 0.01
                double x_coordinate_change=1;
                double x_velocity_change=1;
                double y_velocity_change=1;

                //updating velocities and coordinates
                x_coordinate = results[3][i];
                y_coordinate = results[4][i];

                if(i>1){
                    x_velocity_change = abs(results[1][i]-x_velocity);
                    y_velocity_change = abs(results[2][i]-y_velocity);
                    x_velocity = results[1][i];
                    y_velocity= results[2][i];
                    x_coordinate_change = abs(results[3][i]-results[3][i-1]);
                    y_coordinate_change = abs(results[4][i]-results[4][i-1]);
                }

                // checking for stopping condition
                double pd_value_dx = height_PartialDerivative.calculatePD_notation("dh/dx",x_coordinate,y_coordinate);
                double pd_value_dy = height_PartialDerivative.calculatePD_notation("dh/dy",x_coordinate,y_coordinate);
                MatrixMapArea obj = mapManager.accessObject(x_coordinate,y_coordinate);
                stoppingCondition = checkStoppingConditions(obj,golfBall,pd_value_dx,pd_value_dy,x_velocity_change,y_velocity_change,x_velocity,y_velocity, x_coordinate, y_coordinate,x_coordinate_change,y_coordinate_change);
                // updating frictions for new position if the ball is on AreaType area
                area =  mapManager.accessObject(x_coordinate,y_coordinate);
                if(area instanceof AreaType && stoppingCondition.isEmpty() ){
                    double new_kf = ((AreaType) area).getKineticFriction();
                    if(current_kf!=new_kf){
                        // repeat calculations for new friction
                        current_kf = new_kf;
                        in_Conditions[1] = x_velocity;
                        in_Conditions[2] = y_velocity;
                        in_Conditions[3] = x_coordinate;
                        in_Conditions[4] = y_coordinate;
                        in_Conditions[5] = current_kf;
                        path_coordinates_X.add(x_coordinate);
                        path_coordinates_Y.add(y_coordinate);
                        break;
                    }
                }
                // saves coordinates to final path ( includes coordinates where ball hit the obstacle )
                path_coordinates_X.add(x_coordinate);
                path_coordinates_Y.add(y_coordinate);

                // ends calculations if stopping condition present
                if(!stoppingCondition.isEmpty()){
                    break;
                }
            }
            // updating initial condition for next loop
            in_Conditions[1] = x_velocity;
            in_Conditions[2] = y_velocity;
            in_Conditions[3] = x_coordinate;
            in_Conditions[4] = y_coordinate;
            in_Conditions[5] = current_kf;

            if(!stoppingCondition.isEmpty()){
                customTimeAndStepSize = false;
                break;
            }
        }
        // Returning the list of coordinates
        double[][] finalPath = new double[2][path_coordinates_X.size()];
        for (int i = 0; i < path_coordinates_X.size(); i++) {
            finalPath[0][i] = path_coordinates_X.get(i);
            finalPath[1][i] = path_coordinates_Y.get(i);
        }
        //System.err.println(Arrays.toString(finalPath[0]));
        //System.err.println(Arrays.toString(finalPath[1]));
        // stepSize is timeInterval as it is used as  chang of time in ODE solver
        return new CoordinatesPath(finalPath,stepSize,stoppingCondition);
    }



    /**
     * Checks if moving object:
     * - hits an obstacle
     * - stops moving due to friction
     */
    public String checkStoppingConditions(MatrixMapArea obj, GolfBall golfBall, double dh_dx, double dh_dy, double x_velocity_change, double y_velocity_change, double x_velocity_last, double y_velocity_last, double x_coordinate, double y_coordinate,double x_coordinate_change,double y_coordinate_change){

        if(ballInHole( x_velocity_last, y_velocity_last,  x_coordinate,  y_coordinate)){
            return "ball_in_the_hole";
        }
        switch (obj) {
            case null -> {
                return "outside_of_playable_area";
            }
            case ObstacleType obstacleType -> {
                return "obstacle_hit";
            }
            case AreaType areaType -> {
                double formula_value = Math.sqrt(dh_dx * dh_dx + dh_dy * dh_dy)+1;
                //System.err.println(areaType.getStaticFriction() +" > "+ formula_value);
                //System.err.println((formula_value ==1) +" " +(x_velocity_change) +" " + (y_velocity_change));
                if (areaType.getStaticFriction() > formula_value)  {
                  //  System.err.println(areaType.getStaticFriction() +" > "+ formula_value);
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
        GolfBall golfBallPlayer1= new GolfBall(0.0,0.0,0.0459,0.15);
        ArrayList<GolfBall> golf_balls =  new ArrayList<GolfBall>();
        golf_balls.add(golfBallPlayer1);
        golfBallPlayer1.setPosition(2,2);
        MapManager mapManager = new MapManager();
        mapManager.generateTerrainData();
        PhysicsEngine engine =  new PhysicsEngine();

        CoordinatesPath results = engine.calculateCoordinatePath(golf_balls.get(0),2,2);
        double[][] path = results.getPath();
       //// System.out.println("_____X__");
       //// System.out.println(Arrays.toString(path[0]));
       //// System.out.println("_____Y__");
       //// System.out.println(Arrays.toString(path[1]));
       //// System.out.println("___Stopping_Condition___");
        System.out.println(results.getStoppingCondition());
    }
}

