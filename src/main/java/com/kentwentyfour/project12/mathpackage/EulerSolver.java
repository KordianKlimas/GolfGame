package com.kentwentyfour.project12.mathpackage;

import java.util.ArrayList;
import java.util.List;

/**
 * The EulerSolver class implements the ODESolverInterface to solve
 * ordinary differential equations (ODEs) using the Euler method.
 */
public class EulerSolver implements ODESolverInterface {
    private List<PartialDerivative> partialDerivatives;

    /**
     * Constructs a new EulerSolver with an empty list of partial derivatives.
     */
    public EulerSolver() {
        this.partialDerivatives = new ArrayList<>();
    }

    /**
     * Solves the given system of ODEs using the Euler method.
     *
     * @param equation      an array of equations representing the system of ODEs.
     * @param stepSize      the step size for the Euler method.
     * @param inTime        the total time for which the ODEs are solved.
     * @param inConditions  an array of initial conditions for the ODEs.
     * @param variables     a list of variable names corresponding to the ODEs.
     * @return              a 2D array containing the results of the ODE solution.
     */
    @Override
    public Double[][] solve(String[] equation, double stepSize, int inTime, double[] inConditions, List<String> variables) {
        FormulaCalculator calc = new FormulaCalculator();
        for (PartialDerivative pd : partialDerivatives) {
            calc.addPartialDerivative(pd);
        }

        Double[][] results = new Double[equation.length + 1][(int) (inTime / stepSize) + 1];
        List<List<String>> equationList = new ArrayList<List<String>>();

        for (int i = 0; i < equation.length; i++) {
            // Transform the string[] equation array into a List of Lists of String
            // so that later we can use the same calculator
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
            // Updating partial derivatives for x, y
            for (int j = 1; j < results.length; j++) {
                // Calculating the next points
                results[j][i] = results[j][i - 1] + (calc.calculateRPN(equationList.get(j - 1)) * stepSize);
                // Xn+1 = Xn + (X'n * StepSize)
            }
            for (int k = 0; k < results.length; k++) {
                // Updating the values of the variables with the points calculated
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
