package com.kentwentyfour.project12.mathpackage;

import com.kentwentyfour.project12.mathpackage.ODESolverInterface;
import com.kentwentyfour.project12.mathpackage.PartialDerivative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ODESolver {
    private static List<PartialDerivative> partialDerivatives = new ArrayList<>();
    private ODESolverInterface solver;

    public ODESolver(ODESolverInterface solver) {
        this.solver = solver;
    }

    public void addPartialDerivative(PartialDerivative pd) {
        boolean found = false;
        for (PartialDerivative storedPd : partialDerivatives) {
            if (storedPd.getName().equals(pd.getName())) {
                if (!storedPd.getParsedEquation().equals(pd.getParsedEquation())) {
                    storedPd.setParsedEquation(pd.getParsedEquation());
                }
                found = true;
                break;
            }
        }
        if (!found) {
            partialDerivatives.add(pd);
        }
    }

    public Double[][] solve(String[] equations, double stepSize, int inTime, double[] inConditions, List<String> variables) {
        return solver.solve(equations, stepSize, inTime, inConditions, variables);
    }

    public static void main(String[] args) {
        ODESolver eulerSolver = new ODESolver(new EulerSolver());
        ODESolver rungeKuttaSolver = new ODESolver(new RungeKuttaSolver());

        long startTime = System.nanoTime();


        String[] equations = new String[] {
                "-9.81 * dh/dx / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vx / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )", // dvx/dt
                "-9.81 * dh/dy / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vy / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )",  // dvy/dt
                "vx",  // dx/dt
                "vy",  // dy/dt
        };
        double stepSize = 0.1;
        int in_Time = 10;

        double[] in_Conditions = {0,2,2,2,0,0.1};
        List<String> variables = Arrays.asList( "t","vx","vy","x","y","k_f");
        //PartialDerivative pd = new PartialDerivative("h","0,4  * ( 0.9 - 2.71828 ^ ( ( x ^ 2 + y ^ 2 ) / -8 ) )","x","y");
        PartialDerivative pd = new PartialDerivative("h","sin( ( x - y ) / 7 ) + 0.5","x","y");
        eulerSolver.addPartialDerivative(pd);
        rungeKuttaSolver.addPartialDerivative(pd); //test

        Double[][] results= rungeKuttaSolver.solve(equations, stepSize, in_Time, in_Conditions, variables);


        for(int i=0; i< 100; i++){
            results = rungeKuttaSolver.solve(equations, stepSize, in_Time, in_Conditions, variables);
        }
        in_Conditions[1]=3;
        //ODESolver.addPartialDerivatives(pd);
        in_Conditions[2]=3;

        //System.out.println(Arrays.toString(results[0]));
        //System.out.println("_______");
        //System.out.println(Arrays.toString(results[1]));
        //System.out.println("_______");
        //System.out.println(Arrays.toString(results[2]));
        //System.out.println("_____X__");
        //System.out.println(Arrays.toString(results[3]));
        //System.out.println("_____Y__");
        System.out.println(Arrays.toString(results[4]));

        long endTime = System.nanoTime();

        // Calculate elapsed time in milliseconds
        double durationInMillis = (endTime - startTime) / 1_000_000.0;

        // Print the duration
        System.out.println("Execution time: " + durationInMillis + " milliseconds");

        //  ODESolver.addPartialDerivative(pd2); //test

    }
}