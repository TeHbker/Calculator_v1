package com.shpp.p2p.cs.vtolmachov.assignment10;


import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class Assignment10Part1 {

    private static final Map<String, Integer> OPERATORS_PRIORITY = Map.of("+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "^", 3,
            "(", 0,
            ")", 0);

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            System.out.println("Answer: " + calculate(args));
        }

    }

    /**
     * Main method that's calculates formula that we get from console
     *
     * @param args incoming parameters with "formula" "arg1=number" "arg2=number" etc
     * @return answer on given formula
     */
    private static double calculate(String[] args) {
        FormulaNormalizer n = new FormulaNormalizer(args);
        ArrayList<String> arrayOfSymbols = n.getFullFormula();
        System.out.println("Formula: " + arrayOfSymbols);
        Stack<Double> stackOfNumbers = new Stack<>();
        Stack<String> stackOfOperators = new Stack<>();

        calculateBasedOnPriority(arrayOfSymbols, stackOfNumbers, stackOfOperators);


        return stackOfNumbers.pop();
    }

    private static void calculateBasedOnPriority(ArrayList<String> arrayOfSymbols, Stack<Double> stackOfNumbers, Stack<String> stackOfOperators) {
        for (String symbol : arrayOfSymbols) {
            if (!symbol.matches("[+\\-*/^()]")) {
                stackOfNumbers.push(Double.parseDouble(symbol));
            } else {
                if (stackOfOperators.isEmpty() || symbol.equals("(")) {
                    stackOfOperators.push(symbol);
                } else if (symbol.equals(")")) {

                    emptyLeftOperators(stackOfOperators, stackOfNumbers);
                    stackOfOperators.pop(); // This is for the last symbol that appears and its 100% is "("

                } else {

                    if (OPERATORS_PRIORITY.get(symbol) <= OPERATORS_PRIORITY.get(stackOfOperators.peek())) {
                        stackOfNumbers.push(operateTwoNumbers(stackOfOperators.pop(), stackOfNumbers.pop(), stackOfNumbers.pop()));
                    }
                    stackOfOperators.push(symbol);

                }

            }
        }
        emptyLeftOperators(stackOfOperators, stackOfNumbers);
    }

    private static void emptyLeftOperators(Stack<String> stackOfOperators, Stack<Double> stackOfNumbers) {
        while (!stackOfOperators.isEmpty()) {
            if (stackOfOperators.peek().equals("(")) {
                break;
            }
            stackOfNumbers.push(operateTwoNumbers(stackOfOperators.pop(), stackOfNumbers.pop(), stackOfNumbers.pop()));
        }
    }

    private static double operateTwoNumbers(String symbol, double num2, double num1) {
        return switch (symbol) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            case "/" -> num1 / num2;
            case "^" -> Math.pow(num1, num2);
            default -> throw new IllegalStateException("Unexpected value: " + symbol);
        };
    }

}

