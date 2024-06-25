package com.kentwentyfour.project12.mathpackage;

import java.util.List;

/**
 * The ODESolverInterface provides methods for solving systems of ordinary differential equations (ODEs).
 */
public interface ODESolverInterface {
    /**
     * Solves the given system of ODEs using a specified numerical method.
     *
     * @param equations     an array of equations representing the system of ODEs.
     * @param stepSize      the step size for the numerical method.
     * @param inTime        the total time for which the ODEs are solved.
     * @param inConditions  an array of initial conditions for the ODEs.
     * @param variables     a list of variable names corresponding to the ODEs.
     * @return              a 2D array containing the results of the ODE solution.
     */
    Double[][] solve(String[] equations, double stepSize, int inTime, double[] inConditions, List<String> variables);

    /**
     * Adds a partial derivative to the list of partial derivatives.
     *
     * @param partialDerivative  the partial derivative to be added.
     */
    void addPartialDerivative(PartialDerivative partialDerivative);
}
