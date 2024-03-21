package com.example.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class ODESolver  {

    // Function to solve the system of ODEs using Euler's method
    public static Double[][] euler(String[] equation, double stepSize, int in_Time, double[] in_Conditions, List<String> variables){
        FormulaCalculator calc = new FormulaCalculator();

        Double[][] results = new Double[equation.length+1][(int)(in_Time / stepSize)+1];
        List<List<String>> equationList = new ArrayList<List<String>>();

        for(int i = 0; i < equation.length; i++) {
            //transform the string[] equation array into a List of Lists of String
            //this so that later we can use the same calculator
            List<String>temp = calc.parseString(equation[i]);
            equationList.add(temp);
        }

        //setting all the time slots at witch we are measuring
        results[0][0] =0.0;
        for(int i = 1; i < results[0].length; i++) {
            results[0][i] = stepSize + results[0][i-1];
        }

        //setting all the initial conditions
        for(int i = 0; i <results.length;i++){
            results[i][0] = in_Conditions[i];
            calc.setIndependendValue(variables.get(i),in_Conditions[i]);
        }

        for(int i = 1; i <results[0].length;i++){
            for(int j = 1; j < results.length;j++){

                //calculating the next points

                results[j][i] = results[j][i-1] + (calc.cacluateRPN(equationList.get(j-1)) * stepSize);

                //  Xn+1 = Xn + (X'n * StepSize)

            }
            for(int k = 0; k <results.length;k++){
                //updating the values of the variables with the points calculated
                calc.setIndependendValue(variables.get(k),results[k][i]);
            }
        }
        return results;
    }
    public static Double[][] rungeKutta(String[] equation, double stepSize, int in_Time, double[] in_Conditions, List<String> variables) {
        List<Double> tempResults = new ArrayList<Double>();
        FormulaCalculator calc = new FormulaCalculator();
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

        for (int i = 1; i < results[0].length; i++) {
            for (int j = 1; j < results.length; j++) {
                //calculating the next points

                // for k1

                double k1 = stepSize * calc.cacluateRPN(equationList.get(j - 1));

                //for k2
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize/ 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k1 / 2));
                    }
                }
                double k2 = stepSize * calc.cacluateRPN(equationList.get(j - 1));

                //for k3
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize / 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k2 / 2));
                    }
                }
                double k3 = stepSize * calc.cacluateRPN(equationList.get(j - 1));

                //for k4
                for (int k = 0; k < results.length; k++) {
                    //updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize );
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * k3);
                    }
                }
                double k4 = stepSize * calc.cacluateRPN(equationList.get(j - 1));

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


    public static void main(String[] args){
        String[] test1 = {"5 * t"};   // x' = dx/dt = 2 * t
        double stepSize = 0.001;
        double[] initialConditions = {stepSize,0};
        int in_Time = 10;
        List <String> variables = new ArrayList<>();
        variables.add("t");
        variables.add("x");
        Double[][] solutions =rungeKutta(test1,stepSize,in_Time, initialConditions,variables);

        //printing out the solution to sse the results
        for (int i = 1; i <solutions.length; i++ ){
            for(int j = 0; j <solutions[0].length;j++){
                System.out.print(solutions[i][j]+" ");
            }
            System.out.println();
        }
    }
}

