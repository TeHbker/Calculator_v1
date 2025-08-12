package com.shpp.p2p.cs.vtolmachov.assignment10;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Assignment10Part1 {

    private static final HashMap<String, Integer> operatorsPriority = new HashMap<>();

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            System.out.println("Answer: " + calculate(args));
        }

    }

    /**
     * Main method that's calculates formula that we get from console
     * @param   args incoming parameters with "formula" "arg1=number" "arg2=number" etc
     * @return  answer on given formula
     */
    private static double calculate(String[] args) {
        FormulaNormalizer n = new FormulaNormalizer(args);
        ArrayList<String> arrayOfSymbols = n.getFullFormula();
        // System.out.println("Formula: " + arrayOfSymbols);
        Stack<Double> stackOfNumbers = new Stack<>();
        Stack<String> stackOfOperators = new Stack<>();

        createPriorityMapOfOperators();
        calculateBasedOnPriority(arrayOfSymbols, stackOfNumbers, stackOfOperators);
        emptyLeftOperators(stackOfOperators, stackOfNumbers);

        return stackOfNumbers.pop();
    }

    private static void calculateBasedOnPriority(ArrayList<String> arrayOfSymbols, Stack<Double> stackOfNumbers, Stack<String> stackOfOperators) {
        for (String symbol : arrayOfSymbols) {
            if (!symbol.matches("[+\\-*/^()]")) {
                stackOfNumbers.push(Double.parseDouble(symbol));
            } else {
                if (stackOfOperators.isEmpty() || symbol.equals("(") || symbol.equals(")")) {
                    stackOfOperators.push(symbol);
                } else {
                    /*
                    TODO: Виправити помилку -(12 + 12+ 12). Краще прописати умову типу якщо одне число і один опер - множити на 1
                     */
                    if (stackOfOperators.peek().equals(")")) {
                        specialConditionsForBrackets(stackOfNumbers, stackOfOperators);
                    }

                    if (operatorsPriority.get(symbol) <= operatorsPriority.get(stackOfOperators.peek())) {
                        stackOfNumbers.push(operateTwoNumbers(stackOfOperators.pop(), stackOfNumbers.pop(), stackOfNumbers.pop()));
                    }
                    stackOfOperators.push(symbol);

                }

            }
        }
    }

    private static void emptyLeftOperators(Stack<String> stackOfOperators, Stack<Double> stackOfNumbers) {
            while (!stackOfOperators.isEmpty()) {
                stackOfNumbers.push(operateTwoNumbers(stackOfOperators.pop(), stackOfNumbers.pop(), stackOfNumbers.pop()));
            }
    }

    private static double operateTwoNumbers(String symbol, double num2, double num1) {
        double answ = switch (symbol) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            case "/" -> num1 / num2;
            case "^" -> Math.pow(num1, num2);
            default -> throw new IllegalStateException("Unexpected value: " + symbol);
        };
        return answ;
    }

    private static void createPriorityMapOfOperators() {
        operatorsPriority.put("+", 1);
        operatorsPriority.put("-", 1);
        operatorsPriority.put("*", 2);
        operatorsPriority.put("/", 2);
        operatorsPriority.put("^", 3);
        operatorsPriority.put("(", 0);
        operatorsPriority.put(")", 0);
    }

}


