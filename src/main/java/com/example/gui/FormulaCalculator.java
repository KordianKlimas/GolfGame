package com.example.gui;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.example.gui.Utilities;

import static com.example.gui.Utilities.*;

/**
 * This class is for calculating formulas given as a string.
 * <p>
 * <strong>RULES FOR INPUT:</strong>
 * <ul>
 *     <li>Between every element, there must be a space: <em>good</em> -> [1 + 3 ( 2 ^ -4 ) * 3], <em>bad</em> -> [1+ 3( 2^-4 ) *3]</li>
 *     <li>No support for more than one sign [+ - 2....] or [- - 2 ...]. <em>good</em> -> [ - ( 4 - 7 ) ], <em>bad</em> -> [- ( - ( 3 + 3 ) +3  ....)]</li>
 *     <li>If a number is negative, there must be no space: <em>good</em> -> [ -1 ], <em>bad</em> -> [ - 1 ]</li>
 *     <li>You can use '.' and ',' for decimals, e.g., [ 2,3 + 3 - 1.1 * 2,4]</li>
 *     <li>Square root supported: sqrt( 4 )</li>
 * </ul>
 * </p>
 * <p>
 * <strong>How to use:</strong>
 * <ol>
 *     <li>Create a {@code List<String>} that will store the parsed formula.</li>
 *     <li>Parse the input string and save it to the {@code List<String>}: {@code parseString(String)}.</li>
 *     <li>Add initial conditions/solutions: {@code setIndependentValue("X", 1)}.</li>
 *     <li>Calculate, returns double: {@code calculateRPN(List<String>)}.</li>
 * </ol>
 * </p>
 *
 * @author Kordian
 */
public class FormulaCalculator{
    //contains the independent values / initial value
    HashMap<String, Double> independentValues = new HashMap<String, Double>();


    // Converted String to Stack for forumlas
    //  reverse Polish Notation Using Stacks
    //  https://www.youtube.com/watch?v=QxHRM0EQHiQ ex.3 mistake [63*45 - - 2+] not [63*45 - 2+]

    /**
     * Parses a mathematical expression into a list of tokens.
     *
     * This method takes a mathematical expression string and converts it into a list
     * of tokens using the Shunting Yard algorithm. The tokens represent the expression
     * in Reverse Polish Notation (RPN), making it easier to evaluate.
     *
     * @param equation_string The input mathematical expression to be parsed
     * @return A list of tokens representing the parsed expression in RPN
     * @throws IllegalArgumentException if the input string is null or empty
     */
    public List<String> parseString(String equation_string){

        String[] equation_elements = equation_string.replaceAll(",", ".").split(" ");    // split elements inside of formula (by space) ex. of proper formula: "X ( 4 + Y ) + 12 / 3" - X independed var , Y  independed var from diffrent equation
        Stack<String> stack = new Stack<String>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < equation_elements.length; i++) {

            String element= equation_elements[i]; // number or element named sybmol

            //
            // if the elements a number or dynamic variable (independent variable from diffrent formula)
            // or just independent variable
            //
            if(isNumeric(element)){
                list.add(element);
            }
            else if(!(element.equals("") ||element.equals("+") || element.equals("-") || element.equals("*") || element.equals("/") || element.equals("(") || element.equals(")") || element.equals("^") || element.equals("sqrt("))){
                list.add(element); // sometimes when space is first in string the split method treats it as a seperator and add null value
            }

            else{
                element =  equation_elements[i];

                switch (element) { // the '(' is unique. If '(' occurs we continue normally  untill ')'
                    case "+":
                    case "-":
                        // if stack is  empty
                        // or
                        // stack.peek() is equal to '(' then stack.push(element)
                        //
                        // else transfer to list all elements with higher or equal precedence until '(' or empty stack and then stack.push(element)
                        //                                         [+ - / * ^]
                        //

                        if(stack.isEmpty() || stack.peek().equals("(") || stack.peek().equals("sqrt(")){
                            stack.push(element);
                        }else{
                            while (!(stack.isEmpty()) && !(stack.peek().equals("(") && !(stack.peek().equals("sqrt(")))) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }
                        break;
                    case "/":
                    case "*":
                        //
                        // if stack is  empty
                        // or
                        // if stack.peek() is equal to '(' then  stack.push(element)
                        // else transfer to list all elements with higher or equal precedence until '(' or empty stack and stack.push(element)
                        //                                          [/  *  ^]
                        // else (if no higher or equal precedence elements in stack) just add to stack

                        if(stack.isEmpty() || stack.peek().equals("(") || stack.peek().equals("sqrt(")){
                            stack.push(element);
                        }else if (stack.peek().equals("/") || stack.peek().equals("*") || stack.peek().equals("^")) {
                            while (!(stack.isEmpty()) && !(stack.peek().equals("(") || stack.peek().equals("sqrt(")||stack.peek().equals("-")||stack.peek().equals("+"))) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }else{
                            stack.push(element);
                        }
                        break;

                    case "^":
                        //
                        // if stack is  empty
                        // or
                        // if stack.peek() is equal to '(' then  stack.push(element)
                        // else transfer to list all elements with higher or equal precedence until '(' or empty stack and stack.push(element)
                        //                                          [^]
                        //
                        if(stack.isEmpty() || stack.peek().equals("(")){
                            stack.push(element);
                        }else{// here is the mistake
                            while (!(stack.isEmpty()) && stack.peek().equals("^")) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }
                        break;
                    case "sqrt(":
                        stack.push(element);
                        break;
                    case "(":    // just add it :D
                        stack.push(element);
                        break;
                    case ")":
                        //
                        //  Transfer to list every element until ')' or sqrt (  then pop the ')' and put all elments between the  ( ) to list and pop ( or add sqrt(
                        //
                        //
                        while ( !stack.isEmpty() && !(stack.peek().equals("(")) && !(stack.peek().equals("sqrt("))) {
                            list.add(stack.pop());
                        }
                        if(stack.peek().equals("sqrt(")){    // in case it's sqrt we have to add it to parsed list
                            list.add(stack.pop());
                        }else{
                            stack.pop();
                        }

                        break;
                }
            }
        }
        // tranfer all elements to list
        while (!stack.isEmpty()) {
            list.add(stack.pop());
        }
        return list;
    }

    // variables setter
    /**
     * Sets the value of an independent (dynamic) variable.
     *
     * @param name  The name of the independent variable
     * @param value The value to be assigned to the variable
     */
    public void setIndependendValue(String name,double value){
        independentValues.put(name, value);
    }

    // calculate RPN using stacks
    // https://www.youtube.com/watch?v=QxeSjGFxjyk 12 min
    /**
     * Calculates the result of a mathematical expression in Reverse Polish Notation (RPN) using stacks.
     * <p>
     * This method evaluates the given mathematical expression, which is provided in RPN format, using a stack-based algorithm.
     * It supports basic arithmetic operations (+, -, *, /), exponentiation (^), and square root (sqrt).
     * The method also handles dynamic variables by looking them up in the provided dictionary of independent values.
     * </p>
     * <p>
     * <strong>Supported Operations:</strong>
     * <ul>
     *     <li>Addition (+)</li>
     *     <li>Subtraction (-)</li>
     *     <li>Multiplication (*)</li>
     *     <li>Division (/)</li>
     *     <li>Exponentiation (^)</li>
     *     <li>Square Root (sqrt)</li>
     * </ul>
     * </p>
     *
     * @param list The list of tokens representing the mathematical expression in RPN
     * @return The result of the evaluation
     */
    public double cacluateRPN(List<String> list){
        Stack<Double> stack = new Stack<Double>();
        String peekedElem = "";
        for(int i = 0; i<list.size(); i++){
            peekedElem =  list.get(i);
            if(isNumeric(peekedElem)){ // check for numbers -> push
                stack.push(strToDouble(peekedElem));
            }
            else if(!(peekedElem.equals("+")||peekedElem.equals("-")||peekedElem.equals("*")||peekedElem.equals("/")||peekedElem.equals("^")||peekedElem.equals("sqrt("))){ // check for dynamic variables
                //it's variable
                if (independentValues.containsKey(peekedElem)){
                    stack.push(independentValues.get(peekedElem));
                }else{
                    System.out.println("there is no:  " + peekedElem +"  in independentValues dictionary method: cacluateRPN");
                }

            }
            else{
                double elem1;
                double elem2;
                switch (peekedElem) {

                    case "+":
                        stack.push(stack.pop() +  stack.pop());
                        break;
                    case "-":
                        elem1=stack.pop();
                        // this can be improved to support -(-( .... ))   ([... - - -])
                        //if the previous element is +  or -
                        if( i<list.size() && (list.get(i-1).equals("+") || list.get(i-1).equals("-"))){
                            stack.push(-elem1);
                        }//else if(i<list.size() && list.get(i-1).equals("-")){//if the previous element is -
                        //    stack.push(-elem1);
                        //}
                        else{
                            elem2=stack.pop();
                            stack.push(elem2 - elem1);
                        }
                        break;
                    case "*":
                        stack.push(stack.pop() * stack.pop() );
                        break;
                    case "/":

                        elem1 = stack.pop();
                        elem2 =stack.pop();
                        if(elem2 == 0){
                            System.out.println("ERROR Cannot divide by 0: " +elem2 + "/" + elem1 );
                        }else{
                            stack.push(elem2 / elem1 );
                        }
                        break;
                    case "^":
                        elem1= stack.pop();
                        elem2 =stack.pop();
                        stack.push(Math.pow(elem2, elem1));
                        break;
                    case "sqrt(":
                        if(stack.peek()>= 0){
                            stack.push(Math.sqrt(stack.pop()));
                        }
                        else{
                            System.out.println("cannot calcualte square root of: " + stack.peek());
                        }

                        break;
                    default:
                        break;
                }
            }
        }
        if(stack.isEmpty()){
            System.out.println("Something went wrong while calculating the RPN - stack is empty");
            return 0;
        }else{
            return stack.pop();
        }
    }
}
/*
        // RULES FOR INPUT
        // between every element there must be a space: good-> [1 + 3 ( 2 ^ -4 ) * 3]  bad-> [1+ 3( 2^-4 ) *3]
        // no support for more than one sign [+ -  2....] or [- - 2 ...].  good->[ - ( 4 - 7 ) ],  bad->[- ( - ( 3 + 3 ) +3  ....)]
        // if number is negative there must be no space: good->[ -1 ] , bad->[ - 1 ]
        // you can use . and , for decimals ex. [ 2,3 + 3 - 1.1 * 2,4]
        // Square root supported:  sqrt( 4 )
        // How to use:
        //1. create  List<String> that will store parsed formula
        //2. parse the input String, save it to List<String>: parseString(String)
        //3 add Initial condidtions/solutions:  setIndependendValue("X",1)
        //4 calculate , returns double:  cacluateRPN(List<String>)

        //  suggested  testing formulas
        FormulaCalculator calc = new FormulaCalculator();
        List<String> formulaRPN1 = new ArrayList<>();
        List<String> formulaRPN2 = new ArrayList<>();
        List<String> formulaRPN3 = new ArrayList<>();

        // SIR-model
        // String formula1 = "-2 * S * 2 + 3 * ( 1 - S )";  // -4
        // String formula2 = " 2 * S * I - ( 1 - 2 ) * I";  //  3
        // String formula3 = " 1 * I - 3 * R";  //  -2
        // calc.setIndependendValue("S",1);
        // calc.setIndependendValue("I",1);
        // calc.setIndependendValue("R",1);
        // formulaRPN1 = calc.parseString(formula1);
        // formulaRPN2 = calc.parseString(formula2);
        // formulaRPN3 = calc.parseString(formula3);

        //test for 3 formulas
        // System.out.println(formula1 + "\n" + formula2 + "\n" +formula3);
        // System.out.println(formulaRPN1+ "\n" +formulaRPN2 + "\n" +formulaRPN3);
        // System.out.println(calc.cacluateRPN(formulaRPN1)+ "\n" +calc.cacluateRPN(formulaRPN2) + "\n" +calc.cacluateRPN(formulaRPN3));
        String formula1 = "1 - sqrt( 4 ) ";
        System.out.println(formula1);

        formulaRPN1 = calc.parseString(formula1);
        System.out.println(formulaRPN1);
        System.out.println(calc.cacluateRPN(formulaRPN1));
*/
