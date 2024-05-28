package com.kentwentyfour.project12.mathpackage;

import java.util.*;

/**
 * Allows to store and calculate partial derivative with use of Three-point centered difference method
 */
public class PartialDerivative {
    FormulaCalculator calc = new FormulaCalculator();
    List<String> parsedEquation = new ArrayList<>();
    ArrayList<String> variables = new ArrayList<>();
    HashMap<String,Double> variables_values = new HashMap<>();

    double stepSize = 0.1;
    private String name;

    /**
     * Takes equation of PD and dynamic variables.
     * @param name - the String denoting the formula ex: h = x ^ 2 + 3 * y // h is name dh/dy is derivative

     */
    public PartialDerivative(String name){
        this.name = name;
    }
    /**
     * Takes equation of PD and dynamic variables.
     * @param name - the String denoting the formula ex: h = x ^ 2 + 3 * y // h is name dh/dy is derivative
     * @param equation
     * @param Variables
     */

    public PartialDerivative(String name,String equation, String... Variables){
        this.name = name;
        parsedEquation = calc.parseString(equation);
        for(String x: Variables){
            calc.setIndependendValue(x,0);
            variables.add(x);
            variables_values.put(x,0.0);
        }
    }

    public void setEquation(String equation){
        parsedEquation = calc.parseString(equation);
    }
    public void addVariables(String... Variables){
        for(String x: Variables){
            calc.setIndependendValue(x,0);
            variables.add(x);
            variables_values.put(x,0.0);
        }
    }


    /**
     * Calculates PD with respect to given variable. takes as an argument notation of the formula. ex. dh/dx
     * @param notation
     * @param newVariableValues - values of variables in same order as given in init of object ( must be double )
     * @return double
     */
    public double calculatePD_notation(String notation, Double... newVariableValues){
        // checks for which variable to calculate with respect to
        // find / then takes d out, and we have the variable name
        int index = notation.indexOf("/d");
        String variableName = "";
        if (index != -1 && index + 2 < notation.length()) {
            variableName = notation.substring(index + 2, index + 3);
        } else {
           System.err.println("no variable found in " + notation);
        }
        return calculatePD(variableName, newVariableValues);
    }

    /**
     * Calculates PD with respect to given variable,
     * @param variable
     * @param newVariableValues - values of variables in same order as given in init of object
     * @return double
     */
    public double calculatePD(String variable, Double... newVariableValues){
        // changing values for all variables
        if (newVariableValues.length != variables.size()) {
            System.err.println(Arrays.toString(newVariableValues));
            System.err.println(variables);
            System.err.println("Number of new values doesn't match the number of variables - PartialDerivative");
            return 0;
        }

        for (int i = 0; i < variables.size(); i++) {
            variables_values.put(variables.get(i),newVariableValues[i]);
        }
//;
        //return (f(x + h) - f(x - h)) / (2 * h);
        //      ( f1    -  f2 ) / 2h
        double f1,f2=0;
        String repect_variable ="";
        // f1 = function(x, y + h)
        for (String key : variables_values.keySet()) {
            if(key.equals(variable)){
                calc.setIndependendValue(key,variables_values.get(key)+stepSize); //(variable + stepSize)
                repect_variable =key;
            }else{
                calc.setIndependendValue(key,variables_values.get(key)); // other variables
            }
        }
        f1 =  calc.calculateRPN(parsedEquation);// function(variable + stepSize, y)
        // function(variable - stepSize, y)
        calc.setIndependendValue(repect_variable,variables_values.get(repect_variable)-stepSize); //(variable - stepSize)
        f2 =  calc.calculateRPN(parsedEquation);

        return (f1-f2)/(2*stepSize);
    }



    /**
     *  Allows change of stepsize for more accurate calculation.
     * @param stepSize
     */
    public void changeStepSize(double stepSize ){
        this.stepSize = stepSize;
    }

    /**
     *  Checks if the  notation suits the Pd's formula
     *  @param notation - ex. dy/dx  if formula y = x + ...
     */
    public boolean notationOfPd(String notation) {
        String[] parts = notation.split("/d"); // Split into two parts at most
        if (parts.length == 2) {
            parts[0] = parts[0].substring(1); // Remove the first character (d)
            for(String v: variables){
                if(v.equals(parts[1])&&name.equals(parts[0])){
                    return true;
                }
            }
            System.out.println("Notaion: "+notation+ " is not supported by this Pd: "+ name);
            return false;
        }else {
            System.out.println("Notaion: "+notation+ " is not supported by this Pd: "+ name);
            return false;
        }
    }

    /**
     *
     * @return ArrayList<String> all  used variables
     */
    public ArrayList<String> getvariables(){
        return variables;
    }
    /**
     *Gives variable value used in PD
     * @return double
     */
    public double getVariableValue(String key){
        return variables_values.get(key);
    }
    /**
     *Gives name  od pd
     * @return String
     */
    public String getName(){
        return this.name;
    }

    /**
     *Gives parsed equation
     * @return String
     */
    public List<String> getParsedEquation(){
        return parsedEquation;
    }
    public void setParsedEquation(List<String> parsedEquation){
        this.parsedEquation = parsedEquation;
    }


    public static void main(String[] args){
        PartialDerivative c = new PartialDerivative("h","x ^ 2 + y ^ 2", "x","y");
        System.out.println(c.calculatePD_notation("dh/dy",3.0,3.0));
        System.out.println(c.notationOfPd("dh/dy"));

    }
}
