package com.kentwentyfour.project12.mathpackage;

import java.util.ArrayList;
import java.util.List;

public class EulerSolver implements ODESolverInterface {
    private List<PartialDerivative> partialDerivatives;

    public EulerSolver() {
        this.partialDerivatives = new ArrayList<>();
    }

    @Override
    public Double[][] solve(String[] equation, double stepSize, int inTime, double[] inConditions, List<String> variables) {
        FormulaCalculator calc = new FormulaCalculator();
        for(PartialDerivative pd: partialDerivatives){
            calc.addPartialDerivative(pd);
        }


        Double[][] results = new Double[equation.length+1][(int)(inTime / stepSize)+1];
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
            results[i][0] = inConditions[i];
            calc.setIndependendValue(variables.get(i),inConditions[i]);
        }
        //setting all the additional variables
        for (int i = 0; i < inConditions.length; i++) {
            calc.setIndependendValue(variables.get(i), inConditions[i]);
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

    @Override
    public void addPartialDerivative(PartialDerivative partialDerivative) {
        this.partialDerivatives.add(partialDerivative);
    }
}