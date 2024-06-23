package com.kentwentyfour.project12.mathpackage;

import java.util.List;

public interface ODESolverInterface {
    Double[][] solve(String[] equations, double stepSize, int inTime, double[] inConditions, List<String> variables);
    void addPartialDerivative(PartialDerivative partialDerivative);
}
