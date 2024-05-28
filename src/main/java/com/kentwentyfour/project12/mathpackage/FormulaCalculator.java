package com.kentwentyfour.project12.mathpackage;

import java.util.*;

import static com.kentwentyfour.project12.Utilities.*;



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
    ArrayList<PartialDerivative>  partialDerivatives = new ArrayList<>();
    // set of all operations using  parenthesis
    Set<String> opererationsSet = new HashSet<>(List.of(
            "sqrt(", "sin(", "cos(", "tg(", "ctg(", "ln("
    ));


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
     * <strong>Supported Operations:</strong>
     * <ul>
     *     <li>Addition (+)</li>
     *     <li>Subtraction (-)</li>
     *     <li>Multiplication (*)</li>
     *     <li>Division (/)</li>
     *     <li>Exponentiation (^)</li>
     *     <li>Square Root (sqrt(...))</li>
     *     <li>Natural Logarithm (ln(...))</li>
     *     <li>Sine (sin(...))</li>
     *     <li>Cosine (cos(...))</li>
     *     <li>Tangent (tan(...))</li>
     *     <li>Cotangent (cot(...))</li>
     * </ul>
     * </p>
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
            }else if(opererationsSet.contains(element)){ // sin cos tg ctg sqrt ln...
                stack.push(element);
            }
            else if(!(element.equals("") ||element.equals("+") || element.equals("-") || element.equals("*") || element.equals("/") || element.equals("(") || element.equals(")") || element.equals("^"))){
                list.add(element);
            }

            else{
                element =  equation_elements[i];

                switch (element) { // the '(' is unique. If '(' occurs we continue normally  untill ')'
                    case "+":
                    case "-":
                        if(stack.isEmpty() || stack.peek().equals("(") || opererationsSet.contains(stack.peek())){
                            stack.push(element);
                        }else{
                            while(!stack.isEmpty() && !stack.peek().equals("(") && !opererationsSet.contains(stack.peek())) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }
                        break;
                    case "/":
                    case "*":
                        if(stack.isEmpty() || stack.peek().equals("(") || opererationsSet.contains(stack.peek())){
                            stack.push(element);
                        }else if (stack.peek().equals("/") || stack.peek().equals("*") || stack.peek().equals("^")) {
                            while (!(stack.isEmpty()) && !(stack.peek().equals("(") || opererationsSet.contains(stack.peek()) ||stack.peek().equals("-")||stack.peek().equals("+"))) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }else{
                            stack.push(element);
                        }
                        break;

                    case "^":
                        if(stack.isEmpty() || stack.peek().equals("(")){
                            stack.push(element);
                        }else{
                            while (!(stack.isEmpty()) && stack.peek().equals("^")) {
                                list.add(stack.pop());
                            }
                            stack.push(element);
                        }
                        break;
                    case "(":
                        stack.push(element);
                        break;
                    case ")":

                        while (!stack.isEmpty() && !(stack.peek().equals("(")) && !(opererationsSet.contains(stack.peek()))) {

                            list.add(stack.pop());

                        }
                        if (opererationsSet.contains(stack.peek())) {    // in case element is in operationSet we have to add it to parsed list
                            list.add(stack.pop());
                        } else {
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
    public void addPartialDerivative(PartialDerivative pd){
        partialDerivatives.add(pd);
        for(String var:pd.getvariables()){
            independentValues.put(var,pd.getVariableValue(var));
        }
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
     *
     * @param list The list of tokens representing the mathematical expression in RPN
     * @return The result of the evaluation
     */
    public double calculateRPN(List<String> list){

        Stack<Double> stack = new Stack<Double>();
        String peekedElem = "";
        HashMap<String,Double> pd_calculated = new HashMap<>(); // stores  calculated pd's  to save computation time.
        for(int i = 0; i<list.size(); i++){
            peekedElem =  list.get(i);
            if(isNumeric(peekedElem)){ // check for numbers -> push
                stack.push(strToDouble(peekedElem));
            }
            else if(!(peekedElem.equals("+")||peekedElem.equals("-")||peekedElem.equals("*")||peekedElem.equals("/")||peekedElem.equals("^")||opererationsSet.contains(peekedElem))){ // check for dynamic variables
                //it's variable or PD
                if (independentValues.containsKey(peekedElem)){

                    stack.push(independentValues.get(peekedElem));
                }

                else if(!partialDerivatives.isEmpty()){ //replacing the pd with value
                    if(pd_calculated.containsKey(peekedElem)){
                        stack.push(pd_calculated.get(peekedElem));
                    }else{
                        for(PartialDerivative pd:partialDerivatives){
                            if(pd.notationOfPd(peekedElem)){ //if this is correct pd notation calculate it
                                ArrayList<String> arrOfStringVariablesPD =pd.getvariables();
                                Double[] arrOfvariablesPD = new Double[arrOfStringVariablesPD.size()];
                                for( int d = 0; d<arrOfStringVariablesPD.size();d++){
                                    arrOfvariablesPD[d]=(independentValues.get(arrOfStringVariablesPD.get(d)));
                                }
                                pd_calculated.put(peekedElem,pd.calculatePD_notation(peekedElem,arrOfvariablesPD)); // stores calculated pd to save computation time
                                stack.push(pd_calculated.get(peekedElem));
                            }
                        }
                    }
                }
                else{
                    System.err.println("there is no:  " + peekedElem +"  in independentValues dictionary method: calculateRPN");
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
                            stack.push(0.0 );
                           // System.err.println("ERROR Cannot divide by 0: " +elem2 + "/" + elem1 );
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
                            System.err.println("cannot calcualte square root of: " + stack.peek() + " while calculating: ");
                        }
                        break;
                    case "sin(":
                        stack.push(Math.sin(stack.pop()));
                        break;
                    case "cos(":
                        stack.push(Math.cos(stack.pop()));
                        break;
                    case "tg(":
                        stack.push(Math.tan(stack.pop()));
                        break;
                    case "ctg(":
                        stack.push(1.0 / Math.tan(stack.pop()));
                        break;
                    case "ln(":
                        if (stack.peek() <= 0) {
                            System.err.println("Error: Natural logarithm is only defined for positive numbers. - FormulaCalculator");
                            stack.push(stack.pop());
                        }else {
                            stack.push(Math.log(stack.pop()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if(stack.isEmpty()){

            return 0;
        }else{
            return stack.pop();
        }
    }
//    public static void main(String[] args){
//        FormulaCalculator calc = new FormulaCalculator();
//        List<String> formulaRPN1 = new ArrayList<>();
//
//        String formula1 = " dh/dy + dh/dy + dh/dy";
//        formulaRPN1 = calc.parseString(formula1);
//        PartialDerivative c = new PartialDerivative("h","x ^ 2 + y","x", "y");
//        calc.addPartialDerivative(c);
//
//        System.out.println(calc.parseString(formula1));
//        System.out.println(calc.calculateRPN(formulaRPN1));
//
//    }
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
        //4 calculate , returns double:  calculateRPN(List<String>)

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
        // System.out.println(calc.calculateRPN(formulaRPN1)+ "\n" +calc.calculateRPN(formulaRPN2) + "\n" +calc.calculateRPN(formulaRPN3));
        String formula1 = "1 - sqrt( 4 ) ";
        System.out.println(formula1);

        formulaRPN1 = calc.parseString(formula1);
        System.out.println(formulaRPN1);
        System.out.println(calc.calculateRPN(formulaRPN1));

*/
