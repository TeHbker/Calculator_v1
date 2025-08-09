package com.shpp.p2p.cs.vtolmachov.assignment11;

import java.util.*;

public class Assignment11Part1 {

    static final String STRING_FOR_REGEX = "[*^/+-]"; // Регулярне значення для пошуку символів
    static ArrayList<String> tokenList; // Main ArrayList with all symbols that appear in entered formula

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            try {
                double answer = 0.0;

                FormulaNormalizer form = new FormulaNormalizer(args);
                String fullFormula = form.getCompletedFormula();
                System.out.println(fullFormula);

                if (fullFormula != null) {
                    answer = calculate(form.getCompletedFormula());
                }
                System.out.println(answer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            testMain();
        }


    }

    /**
     * Method that make priority of operator number-like (^ = 0; * = 1, / = 1, + = 2...)
     * Based on array of that operators (SYMBOLS_WITH_PRIORITY)
     * Potential danger: Might work incorrectly if added pair of symbols with equal priority before ^.
     */
    private static int priorityOfSymbol(String symbol) {
        return switch (symbol) {
            case "^", "sin", "cos", "tg", "atan", "log2", "log10", "sqrt" -> 0;
            case "*", "/" -> 1;
            case "+", "-" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + symbol);
        };
    }


    /**
     * Method that calculate result from Polish Inverse Notation
     *
     * @param formula Clear formula with all variables changed on their value and spaces between all symbols
     * @return Answer on written formula
     */
    public static double calculate(String formula) {
        // Step 1: wrote all symbols to array list, so they will be changed easily
        tokenList = new ArrayList<>(Arrays.asList(formula.split(" ")));
        tokenList = createReversPolNot();
        System.out.println("Reverse Polish Notation: " + tokenList);


        // Step 4: With all operators on its places just calculate two first numbers before each operator
        return makeCalculations();
    }

    /**
     * With all done IPN we start simple calculations in the Array List
     * Program watches for operator, and based on it calculate two previous numbers
     * Then adds result of simple calculations with deletion of two numbers and operator
     *
     * @return Last number that left in the ArrayList
     */
    private static double makeCalculations( ) {
        int i = 0;
        double answer;
        double number1 = 0.0;
        double number2;
        Stack<Double> stack = new Stack<>();
        while (i < tokenList.size()) {
            if (!isOperator(i)) {
                stack.push(Double.parseDouble(tokenList.get(i)));
            } else {
                number2 = stack.pop();
                // For operators-names we need only one number
                if (!tokenList.get(i).matches("[a-zA-Z]+")) {
                    number1 = stack.pop();
                }
                answer = switch (tokenList.get(i)) {
                    case "^" -> Math.pow(number1, number2);
                    case "*" -> number1 * number2;
                    case "/" -> {
                        if (number2 == 0) {
                            throw new ArithmeticException("Division by zero: " + number1 + " / " + number2);
                        } else {
                            yield number1 / number2; // yield is used to return value into "answer"
                        }
                    }
                    case "+" -> number1 + number2;
                    case "-" -> number1 - number2;
                    case "cos" -> Math.cos(number2);
                    case "sin" -> Math.sin(number2);
                    case "tan" -> Math.tan(number2);
                    case "atan" -> Math.atan(number2);
                    case "sqrt" -> {
                        if (number2 < 0) {
                            throw new ArithmeticException("Error! Cannot get sqrt from negative number: " + number2);
                        } else {
                            yield Math.sqrt(number2); // yield is used to return value into "answer"
                        }
                    }
                    case "log2" -> Math.log(number2);
                    case "log10" -> Math.log10(number2);
                    default -> throw new IllegalStateException("Unexpected value: " + tokenList.get(i));
                };
                stack.push(answer);
            }
            i++;
        }
        return stack.pop();
    }

    /**
     * Heart of the program Method that creates Invert Polish Notation
     * Program watch for operator and from that looking for another operator
     * If next operator is bigger first operator moves to it, else stands on its position
     */
    private static ArrayList<String> createReversPolNot( ) {
        ArrayList<String> numbers = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (String symbol : tokenList) {
            if (isOperator(symbol)) {
                if (operators.isEmpty()) {
                    operators.push(symbol);
                } else {
                    if (operators.peek().equals("(")) {
                        operators.push(symbol);
                    } else {
                        // If priority of current symbol in tokenlist < last symbol in Stack of operators
                        if (priorityOfSymbol(symbol) < priorityOfSymbol(operators.peek())) {
                            operators.push(symbol);
                        } else {
                            while (!operators.empty()) {
                                // If there is close brace in Stack we need to empty all operators until we get open brace
                                if (operators.peek().equals("(")) {
                                    break; // This part stands for two or more braces like 2 + (4 - (12 * 3))
                                }
                                numbers.add(operators.pop());
                            }
                            operators.push(symbol);
                        }
                    }
                }
            } else {
                // Special conditions for symbols of "( )"
                if (symbol.equals("(")) {
                    operators.push(symbol);
                } else if (symbol.equals(")")) {
                    while (!operators.empty()) {
                        if (operators.peek().equals("(") || operators.peek().equals(")")) {
                            operators.pop();
                        } else {
                            numbers.add(operators.pop());
                        }
                    }
                } else {
                    numbers.add(symbol);
                }
            }

        }
        // At the end we must push all thats left in the stack
        while (!operators.empty()) {
            numbers.add(operators.pop());
        }
        return numbers;
    }

//    /**
//     * This is one exceptional case for states like 2^3^4 when pow's have different priority
//     *
//     * @param i current operator that we consider
//     * @param j next operator that we consider to compare with first
//     * @return true if two of this operators is pow, false if they're not
//     */
//    private static boolean twoOperatorsIsPow(int i, int j) {
//        return tokenList.get(i).equals("^") && tokenList.get(j).equals("^") && !tokenList.get(tokenList.size() - 2).equals("^");
//    }

    /**
     * Method that check is symbol at i is operator or not
     *
     * @param i Index of current looking symbol in ArrayList
     * @return True if symbol operator, false if not
     */
    private static boolean isOperator(int i) {
        return isOperator(tokenList.get(i));
    }

    /**
     * Overloading method isOperator to check symbols itself, not their index in tokenList
     *
     * @param symbol current symbol that we check
     * @return true if symbol is operator false if not
     */
    private static boolean isOperator(String symbol) {
        return symbol.matches(STRING_FOR_REGEX) || symbol.matches("[a-zA-Z]+");
    }

    public static void testMain( ) {
        // String[] args = {"-a + -b/4 + sin(10) - 5^-2", "a =-3", "b = 5"};
        String[] args = {"(3+2) * (2+3)", "a =-3", "b = 5"};
        // String[] args = {"1+(2+3*(4+5-sin(45*cos(a))))/7", "a =2"};
        // String[] args = {"12 + a + 3", "a =3"};
        // String[] args = {"2 ^ 3", "a =3"}; // -10 +
        // String[] args = {"-10+-3+*12-/3", "a =3"}; // potential error, fuck it!
        // String[] args = {"a+b-c*d", "a=3", "b=1", "c=2", "d=2"}; //
        //String[] args = {"2 ^ -2", "var1 =3"}; //
        main(args);
    }
}

