package com.kentwentyfour.project12.PhysicsEnginePackage;


import com.kentwentyfour.project12.GameObjects.*;
import com.kentwentyfour.project12.MathPackage.FormulaCalculator;
import com.kentwentyfour.project12.MathPackage.ODESolver;
import com.kentwentyfour.project12.MathPackage.PartialDerivative;
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


    public PhysicsEngine(String CourseProfileFormula, ArrayList<GolfBall> golf_balls,MapManager mapManager){
        //  preparation of  CourseProfileFormula for calculations
        this.mapManager = mapManager;
        this.CPF_parsed =new ArrayList<>(calcCPF.parseString(CourseProfileFormula));
        height_PartialDerivative.setEquation(CourseProfileFormula);
        height_PartialDerivative.addVariables("x","y");
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
        MappableObject area =  mapManager.accessObject(x_coordinate,y_coordinate);
        double current_kf =0.1;
        if(area instanceof AreaType){
            current_kf = ((AreaType) area).getKineticFriction();
        }else{
            System.err.println("The starting position of ball is not playable area");
        }

        // setting up formulas and ODE solver
        String[] equations = new String[] {
                "-9.81 * dh/dx / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vx / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )", // dvx/dt
                "-9.81 * dh/dy / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vy / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )",  // dvy/dt
                "vx",
                "vy"
        };
        List<String> variables = Arrays.asList( "t","vx","vy","x", "y","k_f");
        ODESolver.addPartialDerivative(height_PartialDerivative);
        double stepSize = 0.1;
        int in_Time = 10;
        double[] in_Conditions = {0,velocityX, velocityY,x_coordinate,y_coordinate,current_kf};

        // calculating new coordinates until any stopping condition is met
        LinkedList<Double> path_coordinates_X = new LinkedList<>();
        LinkedList<Double> path_coordinates_Y = new LinkedList<>();

        String stoppingCondition="";
        while (stoppingCondition.isEmpty()){
            Double[][] results = ODESolver.rungeKutta(equations, stepSize, in_Time, in_Conditions, variables);
            //System.out.println(Arrays.toString(results[4]));
            double  x_velocity =  results[1][0];
            double  y_velocity =  results[2][0];

            for(int i=0;i<results[0].length;i++ ){

                //updating velocities and coordinates
                double x_velocity_change=1;
                double y_velocity_change=1;
                if(i>1){
                    x_velocity_change = abs(results[1][i]-x_velocity);
                    y_velocity_change = abs(results[2][i]-y_velocity);
                    x_velocity = results[1][i];
                    y_velocity= results[2][i];
                }
                x_coordinate = results[3][i];
                y_coordinate = results[4][i];
              //  System.out.println("X: "+ x_coordinate+"Y "+y_coordinate);

                // updating frictions for new position
                area =  mapManager.accessObject(x_coordinate,y_coordinate);
                if(area instanceof AreaType){
                    current_kf = ((AreaType) area).getKineticFriction();
                }

                // checking for stopping condition
                double pd_value_dx = height_PartialDerivative.calculatePD_notation("dh/dx",x_coordinate,y_coordinate);
                double pd_value_dy = height_PartialDerivative.calculatePD_notation("dh/dy",x_coordinate,y_coordinate);
                //System.out.println(x_coordinate+" "+y_coordinate);
                MappableObject obj = mapManager.accessObject(x_coordinate,y_coordinate);

                stoppingCondition = checkStoppingConditions(obj,golfBall,pd_value_dx,pd_value_dy,x_velocity_change,y_velocity_change,x_velocity,y_velocity, x_coordinate, y_coordinate);


                path_coordinates_X.add(x_coordinate);
                path_coordinates_Y.add(y_coordinate);

                // end calculations if stopping condition present
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
                break;
            }
        }

        // Returning the list of coordinates
        double[][] finalPath = new double[2][path_coordinates_X.size()];
        for (int i = 0; i < path_coordinates_X.size(); i++) {
            finalPath[0][i] = path_coordinates_X.get(i);
            finalPath[1][i] = path_coordinates_Y.get(i);
        }
        // stepSize is timeInterval as it is used as  chang of time in ODE solver
        return new CoordinatesPath(finalPath,stepSize,stoppingCondition);
    }



    /**
     * Checks if moving object:
     * - hits an obstacle
     * - stops moving due to friction
     */
    public String checkStoppingConditions(MappableObject obj,GolfBall golfBall,double dh_dx, double dh_dy,double x_velocity_change,double y_velocity_change,double x_velocity_last,double y_velocity_last, double x_coordinate, double y_coordinate){

        if(ballInHole( x_velocity_last, y_velocity_last,  x_coordinate,  y_coordinate)){
            return "ball_in_hole";
        }
        switch (obj) {
            case null -> {
                return "outside_of_playable_area";
            }
            case ObstacleType obstacleType -> {
                return "obstacle_hit";
            }
            case AreaType areaType -> {
                double formula_value = Math.sqrt(dh_dx * dh_dx + dh_dy * dh_dy) + 1;
                //System.err.println(areaType.getStaticFriction() +" > "+ formula_value);
                //System.err.println((formula_value ==1) +" " +(x_velocity_change) +" " + (y_velocity_change));
                if (areaType.getStaticFriction() > formula_value)  {
                  //  System.err.println(areaType.getStaticFriction() +" > "+ formula_value);
                    return "static_friction_overcomes_the_force";
                }
                else if(formula_value ==1 && x_velocity_change<0.06 && y_velocity_change<0.06){
                    return "static_friction_overcomes_the_force";
                }
            }
            default -> {

                return "";
            }
        }
        return "";
    }

    public boolean ballInHole(double x_velocity_last,double y_velocity_last, double x_coordinate, double y_coordinate){
        Hole hole = referenceStore.getHole();
        double radiusOfHole = hole.getRadius();
        double x_hole = hole.getX();
        double y_hole = hole.getY();
        double distance = Math.sqrt(Math.pow(x_coordinate - x_hole, 2) + Math.pow(y_coordinate - y_hole, 2));
        System.out.println(distance +" "+x_velocity_last+ " "+y_velocity_last);
        if (distance < radiusOfHole && x_velocity_last<4 && y_velocity_last<4) {
            // Ball in the hole
            return true;
        }
        return false;
    }
    public static void main(String[] args){
        //setting up golf ball/s
        GolfBall golfBallPlayer1= new GolfBall(0.0,0.0,0.0459,0.15);
        ArrayList<GolfBall> golf_balls =  new ArrayList<GolfBall>();
        golf_balls.add(golfBallPlayer1);
        golfBallPlayer1.setPosition(2,2);
        MapManager mapManager = new MapManager("sin( ( x - y ) / 7 ) + 0.5");
        mapManager.generateTerrainData();
        PhysicsEngine engine =  new PhysicsEngine("sin( ( x - y ) / 7 ) + 0.5",golf_balls,mapManager);

        CoordinatesPath results = engine.calculateCoordinatePath(golf_balls.get(0),2,2);
        double[][] path = results.getPath();
        System.out.println("_____X__");
        System.out.println(Arrays.toString(path[0]));
        System.out.println("_____Y__");
        System.out.println(Arrays.toString(path[1]));
        System.out.println("___Stopping_Condition___");
        System.out.println(results.getStoppingCondition());
    }
}

