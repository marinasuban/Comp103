 // This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2020T2, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{
    
    static List<String> operatorsInOrder = Arrays.asList("dist", "sin", "cos", "tan", "log", "ln", "sqrt", "^", "*", "/", "+", "-"); //Order of mathematical operations, used to know when brackets are needed
    static List<String> functions = Arrays.asList("dist", "avg", "sin", "cos", "tan", "log", "ln", "sqrt"); //List of operators that are functions
    
    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the programe - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
                
                if (!Double.isNaN(value)) {
                    printExpr(expr);
                }
                
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }
        /*# YOUR CODE HERE */
        if (expr.getItem()==null) {
            return Double.NaN;
        }
        // PI
        if (expr.getItem().operator.toUpperCase().equals("PI")) {
            return Math.PI;
        }

        if (expr.getItem().operator.toUpperCase().equals("E")) {
            return Math.E;
        }
        //if root is a number - return number
        if (expr.numberOfChildren() == 0 || expr.getItem().operator.equals("#")) {
            return expr.getItem().value;
        }

        //if root is operator
        else {
            String operator = expr.getItem().operator;
            /*# ADD */
            if (operator.equals("+")) { 
                double expressionResult = 0;
                for (GTNode<ExpElem> child : expr) {
                    expressionResult += evaluate(child);
                }
                return expressionResult;
            }

            /*# MINUS */
            else if (operator.equals("-")) { 
                double expressionResult = evaluate(expr.getChild(0));
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    expressionResult -= evaluate(expr.getChild(i));
                }
                return expressionResult;
            }

            /*# MULTIPLY */
            else if (operator.equals("*")) { 
                double expressionResult = 1;
                for (GTNode<ExpElem> child : expr) {
                    expressionResult *= evaluate(child);
                }
                return expressionResult;
            }

            /*# DIVIDE */
            else if (operator.equals("/")) { 
                double expressionResult = evaluate(expr.getChild(0));
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    expressionResult /= evaluate(expr.getChild(i));
                }
                return expressionResult;

            }
            /*# POWER */
            else if (operator.equals("^")) { 
                if(expr.numberOfChildren() != 2) {
                    UI.println("Error: invalid operands for power");
                    return Double.NaN;
                }
                double base = evaluate(expr.getChild(0));
                return Math.pow(base, evaluate(expr.getChild(1)));
                
            }
            /*# SQRT */
            else if (operator.equals("sqrt")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Error: invalid operands for sqrt");
                    return Double.NaN;
                }
                return Math.sqrt(evaluate(expr.getChild(0)));
            }   
            
            /*# Log */
             else if (operator.equals("log")) { 
                if(expr.numberOfChildren() == 2) {
                    return Math.log10(evaluate(expr.getChild(0))) / Math.log10(evaluate(expr.getChild(1)));
                } 
                else if ( expr.numberOfChildren() == 1 ) {
                    return Math.log10(evaluate(expr.getChild(0)));
                }
                else {
                    UI.println("Error: invalid operands for log");
                }
            } else if (operator.equals("ln")) { 
                if ( expr.numberOfChildren() == 1 ) {
                    return Math.log(evaluate(expr.getChild(0)));
                }
                else {
                    UI.println("Error: invalid operands for ln");
                }
            }
            
            /*# SIN COS TAN */ 
            else if (operator.equals("sin")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Error: invalid operands for sin");
                    return Double.NaN;
                }
                return Math.sin(evaluate(expr.getChild(0)));
            } else if (operator.equals("cos")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Error: invalid operands for cos");
                    return Double.NaN;
                }
                return Math.cos(evaluate(expr.getChild(0)));
            } else if (operator.equals("tan")) {
                if(expr.numberOfChildren() != 1) {
                    UI.println("Error: invalid operands for tan");
                    return Double.NaN;
                }
                return Math.tan(evaluate(expr.getChild(0)));
            }
            /*# DIST */
            else if (operator.equals("dist")) {
                if(expr.numberOfChildren() == 4) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double x2 = evaluate(expr.getChild(2));
                    double y2 = evaluate(expr.getChild(3));
    
                    //Pythagoras with absolute values below
                    double x = Math.abs(x1 - x2);
                    double y = Math.abs(y1 - y2);
    
                    double pythag = Math.pow(x, 2) + Math.pow(y, 2);

                    return Math.sqrt(pythag);
                }
                else if (expr.numberOfChildren() == 6) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double z1 = evaluate(expr.getChild(2));
                    double x2 = evaluate(expr.getChild(3));
                    double y2 = evaluate(expr.getChild(4));
                    double z2 = evaluate(expr.getChild(5));
    
                    //Pythagoras with absolute values below
                    double x = Math.abs(x1 - x2);
                    double y = Math.abs(y1 - y2);
                    double z = Math.abs(z1 - z2);
                    
                    double pythag = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
                    return Math.sqrt(pythag);
                }
                else UI.println("Error: invalid operands for dist");
            }
            /*# AVERAGE */
            else if (operator.equals("avg")) {  // division operator
                if(expr.numberOfChildren() < 1) {
                    UI.println("Error: invalid operands for avg");
                    return Double.NaN;
                }

                double ans = 0;
                for (int i = 0; i < expr.numberOfChildren(); i++) {
                    ans += evaluate(expr.getChild(i));
                }
                return ans / expr.numberOfChildren();
            } 
            else
            {
                UI.println("Error: Invalid operator: \"" + expr.getItem() + "\"");
                return Double.NaN;
            }

        }
        return Double.NaN;
    }

    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            try {
                if (sc.hasNext("\\)")) {
                    UI.println("error: missing expression inside brackets");
                    return null;
                }
                ExpElem opElem = new ExpElem(sc.next());  // read the operator
                GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
                while (! sc.hasNext("\\)")){              // loop until the closing ')'
                    GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                    node.addChild(child);                 // and add as a child of the node
                }
                sc.next();  // read and throw away the closing ')'
                return node;
            }
            catch (Exception e ) {
                UI.println("missing closing bracket"); // has opening but not closing bracket
            }                             
            return null;
        }
        else {                                        // next token must be a named constant (PI or E) or a Closing bracket which is an error
            // make a token with the name as the "operator"
            if (sc.hasNext("\\)")) { // if it is a random closing bracket without an opening bracket -> error
                
            }
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }
    
    public static void printExpr(GTNode<ExpElem> expr)
    {
        printExpr(expr, "");
        UI.println();
    }
    
    public static void printExpr(GTNode<ExpElem> expr, String previousOperator)
    {
        if(expr.numberOfChildren() == 0) //if its a number
        {
            UI.print(expr.getItem());
        }
        else {
            //Checks if brackets need to be printed according to the order of operation and function rules
            boolean brackets = (operatorsInOrder.indexOf(expr.getItem().operator) > operatorsInOrder.indexOf(previousOperator) && !previousOperator.equals("") && !functions.contains(previousOperator));
            boolean isFunction = functions.contains(expr.getItem().operator);

            if(isFunction) UI.print(expr.getItem().operator);
            if(brackets || isFunction) UI.print("(");

            for (int i = 0; i < expr.numberOfChildren(); ++i) {
                printExpr(expr.getChild(i), expr.getItem().operator); //Recursively print children
                if(i < expr.numberOfChildren() - 1) //Only print before the last child
                    if(isFunction)
                        UI.print(","); //Prints comma between function parameters if there are more than one
                    else
                        UI.print(expr.getItem().operator); //Print operator between sub equations/values
            }

            if(brackets || isFunction) UI.print(")");
            
        }
    }

}
