package com.example.gui.PhysicsEnginePackage;
import com.example.gui.FormulaCalculator;
import com.example.gui.ODESolver;

import java.util.ArrayList;

public class PhysicsEngine {
    public static void main(String[] args){

        //setting up golf ball/s
        GolfBall golfBallPlayer1= new GolfBall(0,0,0,0.0459);
        ArrayList<GolfBall> golf_balls =  new ArrayList<GolfBall>();
        golf_balls.add(golfBallPlayer1);


        PhysicsEngine PhysicEngine =  new PhysicsEngine("sin( ( x - y ) / 7 ) + 0.5 ",golf_balls);

    }
    // main solver
    ODESolver solver = new ODESolver();
    //  calculator for CourseProfileFormula h(x,y)
    FormulaCalculator calcCPF = new FormulaCalculator();
    ArrayList<String> CPF_parsed = new ArrayList<>();

    PhysicsEngine(String CourseProfileFormula, ArrayList<GolfBall> golf_balls){
        //  preparation of  CourseProfileFormula for calculations
        this.CPF_parsed =new ArrayList<>(calcCPF.parseString(CourseProfileFormula));
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
}

