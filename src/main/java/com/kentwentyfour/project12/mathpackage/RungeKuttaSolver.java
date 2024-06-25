package com.kentwentyfour.project12.mathpackage;

import java.util.ArrayList;
import java.util.List;

/**
 * The RungeKuttaSolver class implements the ODESolverInterface to solve
 * ordinary differential equations (ODEs) using the fourth-order Runge-Kutta method.
 */
public class RungeKuttaSolver implements ODESolverInterface {
    private List<PartialDerivative> partialDerivatives;

    /**
     * Constructs a new RungeKuttaSolver with an empty list of partial derivatives.
     */
    public RungeKuttaSolver() {
        this.partialDerivatives = new ArrayList<>();
    }

    /**
     * Solves the given system of ODEs using the fourth-order Runge-Kutta method.
     *
     * @param equation      an array of equations representing the system of ODEs.
     * @param stepSize      the step size for the Runge-Kutta method.
     * @param inTime        the total time for which the ODEs are solved.
     * @param inConditions  an array of initial conditions for the ODEs.
     * @param variables     a list of variable names corresponding to the ODEs.
     * @return              a 2D array containing the results of the ODE solution.
     */
    @Override
    public Double[][] solve(String[] equation, double stepSize, int inTime, double[] inConditions, List<String> variables) {
        List<Double> tempResults = new ArrayList<Double>();
        FormulaCalculator calc = new FormulaCalculator();
        for (PartialDerivative pd : partialDerivatives) {
            calc.addPartialDerivative(pd);
        }
        Double[][] results = new Double[equation.length + 1][(int) (inTime / stepSize) + 1];
        List<List<String>> equationList = new ArrayList<List<String>>();

        for (int i = 0; i < equation.length; i++) {
            // Transform the string[] equation array into a List of Lists of String
            // this so that later we can use the same calculator
            List<String> temp = calc.parseString(equation[i]);
            equationList.add(temp);
        }
        // Setting all the time slots at which we are measuring
        results[0][0] = 0.0;
        for (int i = 1; i < results[0].length; i++) {
            results[0][i] = stepSize + results[0][i - 1];
        }

        // Setting all the initial conditions
        for (int i = 0; i < results.length; i++) {
            results[i][0] = inConditions[i];
            calc.setIndependendValue(variables.get(i), inConditions[i]);
        }
        // Setting all the additional variables
        for (int i = 0; i < inConditions.length; i++) {
            calc.setIndependendValue(variables.get(i), inConditions[i]);
        }
        for (int i = 1; i < results[0].length; i++) {

            for (int j = 1; j < results.length; j++) {
                // Calculating the next points

                // For k1
                double k1 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                // For k2
                for (int k = 0; k < results.length; k++) {
                    // Updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize / 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k1 / 2));
                    }
                }
                double k2 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                // For k3
                for (int k = 0; k < results.length; k++) {
                    // Updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + (stepSize / 2));
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * (k2 / 2));
                    }
                }
                double k3 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                // For k4
                for (int k = 0; k < results.length; k++) {
                    // Updating the values of the variables with the points calculated
                    if (variables.get(k).equals("t")) {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize);
                    } else {
                        calc.setIndependendValue(variables.get(k), results[k][i - 1] + stepSize * k3);
                    }
                }
                double k4 = stepSize * calc.calculateRPN(equationList.get(j - 1));

                // Save the results in a new List so that the values change only before taking the next step
                tempResults.add(results[j][i - 1] + (k1 + 2 * k2 + 2 * k3 + k4) / 6.0);
            }
            // Putting the new points in their right slot in results
            for (int l = 0; l < tempResults.size(); l++) {
                results[l + 1][i] = tempResults.get(l);
            }
            tempResults.clear();
            // Updating the values of the variables with the points calculated
            for (int k = 0; k < results.length; k++) {
                calc.setIndependendValue(variables.get(k), results[k][i]);
            }
        }
        return results;
    }

    /**
     * Adds a partial derivative to the list of partial derivatives.
     *
     * @param partialDerivative  the partial derivative to be added.
     */
    @Override
    public void addPartialDerivative(PartialDerivative partialDerivative) {
        this.partialDerivatives.add(partialDerivative);
    }
}
