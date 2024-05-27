package com.kentwentyfour.project12.mathpackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Problems:
// the runge kutta solver requiers the t value as first in independent values but dose not use it.
// ODEsolver and PhysicEngine are tight coupled, they should not.
// It would be better if methods were not static, and shared variables
public class ODESolver  {

    // Function to solve the system of ODEs using Euler's method
    static List<PartialDerivative> partialDerivatives =new ArrayList<>();
    /**
     * Solves a system of ordinary differential equations (ODEs) using Euler's method.
     *
     * @param equation     The system of ODEs as an array of strings
     * @param stepSize     The step size used for the numerical integration
     * @param in_Time      The total integration time
     * @param in_Conditions The initial conditions for the system
     * @param variables    The list of independent variables in the equations
     * @return The solution of the system of ODEs as a 2D array of doubles
     * @author Filippo
     */
    public static Double[][] euler(String[] equation, double stepSize, int in_Time, double[] in_Conditions, List<String> variables){
        FormulaCalculator calc = new FormulaCalculator();
        for(PartialDerivative pd: partialDerivatives){
            calc.addPartialDerivative(pd);
        }


        Double[][] results = new Double[equation.length+1][(int)(in_Time / stepSize)+1];
        List<List<String>> equationList = new ArrayList<List<String>>();

        for(int i = 0; i < equation.length; i++) {
            //transform the string[] equation array into a List of Lists of String
            //this so that later we can use the same calculator
            List<String>temp = calc.parseString(equation[i]);
            equationList.add(temp);
        }

        //setting all the time slots at which we are measuring
        results[0][0] =0.0;
        for(int i = 1; i < results[0].length; i++) {
            results[0][i] = stepSize + results[0][i-1];
        }

        //setting all the initial conditions
        for(int i = 0; i <results.length;i++){
            results[i][0] = in_Conditions[i];
            calc.setIndependendValue(variables.get(i),in_Conditions[i]);
        }
        //setting all the additional variables
        for (int i = 0; i < in_Conditions.length; i++) {
            calc.setIndependendValue(variables.get(i), in_Conditions[i]);
        }
        for(int i = 1; i <results[0].length;i++){
            //stopping conditions
            // update partial derviative for x,y
            for(int j = 1; j < results.length;j++){

                //calculating the next points

                results[j][i] = results[j][i-1] + (calc.calculateRPN(equationList.get(j-1)) * stepSize);

                //  Xn+1 = Xn + (X'n * StepSize)

            }
            for(int k = 0; k <results.length;k++){

                //updating the values of the variables with the points calculated
                calc.setIndependendValue(variables.get(k),results[k][i]);
            }
        }
        return results;
    }
    /**
     * Solves a system of ordinary differential equations (ODEs) using the Runge-Kutta method.
     *
     * @param equation     The system of ODEs as an array of strings
     * @param stepSize     The step size used for the numerical integration
     * @param in_Time      The total integration time
     * @param in_Conditions The initial conditions for the system
     * @param variables    The list of independent variables in the equations
     * @return The solution of the system of ODEs as a 2D array of doubles
     * @author Filippo
     */
    public static Double[][] rungeKutta(String[] equation, double stepSize, int in_Time, double[] in_Conditions, List<String> variables) {
        List<Double> tempResults = new ArrayList<Double>();
        FormulaCalculator calc = new FormulaCalculator();
        for(PartialDerivative pd: partialDerivatives){
            calc.addPartialDerivative(pd);
        }
        Double[][] results = new Double[equation.length + 1][(int) (in_Time / stepSize)+1];
        List<List<String>> equationList = new ArrayList<List<String>>();

        for (int i = 0; i < equation.length; i++) {
            //transform the string[] equation array into a List of Lists of String
            //this so that later we can use the same calculator
            List<String> temp = calc.parseString(equation[i]);
            equationList.add(temp);
        }
        //setting all the time slots at witch we are measuring
        results[0][0] = 0.0;
        for (int i = 1; i < results[0].length; i++) {
            results[0][i] = stepSize + results[0][i - 1];
        }

        //setting all the initial conditions
        for (int i = 0; i < results.length; i++) {
            results[i][0] = in_Conditions[i];
            calc.setIndependendValue(variables.get(i), in_Conditions[i]);
        }
        //setting all the additional variables
        for (int i = 0; i < in_Conditions.length; i++) {
            calc.setIndependendValue(variables.get(i), in_Conditions[i]);
        }

        for (int i = 1; i < results[0].length; i++) {

            for (int j = 1; j < results.length; j++) {
                //calculating the next points

                // for k1

                double k1 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                //for k2
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize/ 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k1 / 2));
                    }
                }
                double k2 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                //for k3
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize / 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k2 / 2));
                    }
                }
                double k3 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                //for k4
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize );
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * k3);
                    }
                }
                double k4 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                //I save the results in a new List so that the values change only before taking the next step
                tempResults.add(results[j][i - 1] +  (k1 + 2 * k2 + 2 * k3 + k4)/ 6.0 );
            }
            //Putting the new points in their right slot in results
            for (int l = 0; l < tempResults.size(); l++) {
                results[l + 1][i] = tempResults.get(l);
            }
            tempResults.clear();
            //updating the values of the variables with the points calculated
            for (int k = 0; k < results.length; k++) {
                calc.setIndependendValue(variables.get(k), results[k][i]);
            }
        }
        return results;
    }
    /**
     * This method calculates the derivative at the value x
     * using formula f'(x0) = (f(x0 - deltax) - f(x0)) / deltax
     * @param equation
     * @param variable
     * @param x0
     * @param delta_x
     *@return double
    */
//    public double calculateDerivative(List<String> equation, String variable, double x0, double delta_x){
//        // List <String> equation2 = new ArrayList<>(equation);
//        FormulaCalculator cal = new FormulaCalculator();
//        cal.setIndependendValue(variable, x0);
//        double value_x0 = cal.calculateRPN(equation);
//        cal.setIndependendValue(variable, x0 + delta_x);
//        double  value_x0_plus_deltax = cal.calculateRPN(equation);
//        return (value_x0_plus_deltax - value_x0) / delta_x;
//    }

    /**
     * Adds partial derivative formula to all odesolvers. Makes sure there are only unique formulas.
     * If pd with same name and formula as one in list is added, the stored pd will be replaced.
     * No pd's with same name allowed
     * * @param pd - PartialDerivative
     */

    public static void addPartialDerivative(PartialDerivative pd){
        boolean found = false;
        for (PartialDerivative storedPd : partialDerivatives) {
            if (storedPd.getName().equals(pd.getName())) {
                if (!storedPd.getParsedEquation().equals(pd.getParsedEquation())) {
                    // Update storedPd's parsed equation
                    storedPd.setParsedEquation(pd.getParsedEquation());
                }
                found = true;
                break;
            }
        }
        if (!found) {
            // If not found, add the new PartialDerivative
            partialDerivatives.add(pd);
        }
   }


    public static void main(String[] args){
        String[] equations = new String[] {
                "-9.81 * dh/dx / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vx / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )", // dvx/dt
                "-9.81 * dh/dy / ( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) - k_f * 9.81 / sqrt( 1 + dh/dx ^ 2 + dh/dy ^ 2 ) * vy / sqrt( vx ^ 2 + vy ^ 2  + ( dh/dx * vx + dh/dy * vy ) ^ 2 )",  // dvy/dt
//              "-9.81 * dh/dx - k_f * 9.81 * vx / sqrt( vx ^ 2 + vy ^ 2 )",
//              "-9.81 * dh/dy - k_f * 9.81 * vy / sqrt( vx ^ 2 + vy ^ 2 )",
                "vx",  // dx/dt
                "vy",  // dy/dt
       };

        double stepSize = 0.1;
        int in_Time = 10;
//        double[] in_Conditions = {2, 2, 0, 0, 1, 1, 9.81,0.05};
//        List<String> variables = Arrays.asList( "vx","vy","x", "y","dh/dx","dh/dy","g","k_f");
        double[] in_Conditions = {0,2,2,2,0,0.1};
        List<String> variables = Arrays.asList( "t","vx","vy","x","y","k_f");
        //PartialDerivative pd = new PartialDerivative("h","0,4  * ( 0.9 - 2.71828 ^ ( ( x ^ 2 + y ^ 2 ) / -8 ) )","x","y");
        //PartialDerivative pd = new PartialDerivative("h","sin( ( x - y ) / 7 ) + 0.5","x","y");
        PartialDerivative pd = new PartialDerivative("h","1","x","y");
        PartialDerivative pd2 = new PartialDerivative("h","x ^ 2","x","y");
       // PartialDerivative pd = new PartialDerivative("h","0","x","y");
        ODESolver.addPartialDerivative(pd);
        ODESolver.addPartialDerivative(pd); //test


//        String expressionVx = ((-gdx)/(1+Math.pow(dx, 2)+Math.pow(dy, 2)))+"-"+((mu_kg)/(Math.sqrt(1+Math.pow(dx, 2)+Math.pow(dy, 2))))+"(vx/sqrt(vx^2 + vy^2 + ("+dx+"vx"+"+"+dy+"vy)^2))";
//        String expressionVy = ((-gdy)/(1+Math.pow(dx, 2)+Math.pow(dy, 2))+"-"+(mu_kg)/(Math.sqrt(1+Math.pow(dx, 2)+Math.pow(dy, 2))))+"(vy/sqrt(vx^2 + vy^2 + ("+dx+"vx"+"+"+dy+"vy)^2))";

        Double[][] results = rungeKutta(equations, stepSize, in_Time, in_Conditions, variables);
        in_Conditions[1]=3;
        //ODESolver.addPartialDerivatives(pd);
        in_Conditions[2]=3;
        results = rungeKutta(equations, stepSize, in_Time, in_Conditions, variables);
       //System.out.println(Arrays.toString(results[0]));
       //System.out.println("_______");
       //System.out.println(Arrays.toString(results[1]));
       //System.out.println("_______");
       //System.out.println(Arrays.toString(results[2]));
       //System.out.println("_____X__");
       //System.out.println(Arrays.toString(results[3]));
       //System.out.println("_____Y__");
        System.out.println(Arrays.toString(results[4]));
        ODESolver.addPartialDerivative(pd2); //test
        results = rungeKutta(equations, stepSize, in_Time, in_Conditions, variables);

       // System.out.println(Arrays.toString(results[0]));
       // System.out.println("_______");
       // System.out.println(Arrays.toString(results[1]));
       // System.out.println("_______");
       // System.out.println(Arrays.toString(results[2]));
       // System.out.println("_____X__");
       // System.out.println(Arrays.toString(results[3]));
       // System.out.println("_____Y__");
        System.out.println(Arrays.toString(results[4]));

    }
}

