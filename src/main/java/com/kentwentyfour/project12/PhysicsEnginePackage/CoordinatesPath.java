package com.kentwentyfour.project12.PhysicsEnginePackage;



/**
 * Encapsulates the path of the object along with it's stopping condition and timeInterval
 */
public class CoordinatesPath {
    private double[][] path;
    private String stoppingCondition;  // time between each change of coordinates

    private double timeInterval;  // time between each change of coordinates
    CoordinatesPath(double[][] path,double timeInterval, String stoppingCondition){
        this.path = path;
        this.stoppingCondition = stoppingCondition;
    }
    public double[][]  getPath(){
        return this.path;
    }
    public  double getTimeInterval(){
        return this.timeInterval;
    }
    public String  getStoppingCondition(){
        return this.stoppingCondition;
    }
}
