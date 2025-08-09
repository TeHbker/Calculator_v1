package com.shpp.p2p.cs.vtolmachov.assignment11;

import java.util.HashMap;

public class FormulaNormalizer {
    private final String[] arg;


    /**
     * Constructor that removes spaces after creating a new class of Normalizer
     *
     * @param args Global array that will be figuring in all other operations
     */
    public FormulaNormalizer(String[] args) {
        arg = removeSpaces(args);
    }

    /**
     * Method that normalize introduced version of formula and variables by deleting spaces in them
     *
     * @param args Formula and variables args[0]: x1 + x2 + x3 | args[1] x1 = 1, args[2] x2 = 2, args[3] x3 = 3
     * @return Same array with formula and variables but without spaces
     */
    private String[] removeSpaces(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll(" ", "");
        }

        return args;
    }

    /**
     * Method that parses arg[0] (formula with variables), decides if there is any mistakes in written formula and
     * add spaces between all symbols, so it will be more easily to parse into Array List
     *
     * @return null if formula written wrong or String with formula if its written right way
     */
    public String getFormula( ) {
        String formula = this.arg[0];
        // This part is written 'cause cycle starts with 1, but symbol at 0 can possibly be (
        if (formula.charAt(0) == '(') {
            formula = formula.charAt(0) + " " + formula.substring(1);
        }
        for (int i = 1; i < formula.length(); i++) {
            if (isCharacterOperator(i, formula)) {
                if (formula.charAt(i - 1) == ' ') { // This if for those, who try to write operators like: "12+*5"
                    return null;
                    /*
                    returns null if char before operator is space, 'cause it's means that before is another operator
                     */
                } else {
                    formula = formula.substring(0, i) + " " + formula.charAt(i) + " " + formula.substring(i + 1);
                    i = i + 2;
                }
            } else if (formula.charAt(i) == '-' && formula.charAt(i - 1) != ' ' && formula.charAt(i - 1) != '(') {
                formula = formula.substring(0, i) + " " + formula.charAt(i) + " " + formula.substring(i + 1);
                i = i + 2;
            } else if (formula.charAt(i) == '(') {
                if (formula.charAt(i - 1) != ' ') {
                    // this is used if before ( was none operators something like "sin (30)"
                    formula = formula.substring(0, i) + " " + formula.substring(i);
                } else {
                    formula = formula.substring(0, i + 1) + " " + formula.substring(i + 1);
                }
            } else if (formula.charAt(i) == ')') {
                // by default there is only one space before ) cause than goes operator
                formula = formula.substring(0, i) + " " + formula.substring(i);
                i = i + 1;
            }
        }
        return formula;
    }


    /**
     * Boolean that's watches if character operator (but not including - cause it may be part of the number)
     *
     * @param numberOfChar current position in parsed formula
     * @param formula      full formula in what we watch for character
     * @return true if character operator, false if not
     */
    private boolean isCharacterOperator(int numberOfChar, String formula) {
        String operator = String.valueOf(formula.charAt(numberOfChar));
        return operator.matches("[+*^/]"); // Checks for operators symbol (minus have special check)
    }

    /**
     * Method that split variables like: "a=3" and add it to Hash map with "a" as key and "3" as its value
     *
     * @return simple HashMap with variables and their values
     */
    public HashMap<String, Double> getArguments( ) {
        HashMap<String, Double> vars = new HashMap<>();
        String[] var;
        for (int i = 1; i < arg.length; i++) {
            var = arg[i].split("=");
            /*
            Using var[0] and var[1] 'cause there will be only 2 Strings after splitting them by "="
            */
            vars.put(var[0], Double.valueOf(var[1]));
        }
        if (isVarsContainsReservedValues(vars)) {
            throw new IllegalStateException("Variable are reserved as an operator");
        } else {
            return vars;
        }
    }

    /**
     * Condition to check if user uses names for variables like sin, cos, etc.
     *
     * @param vars HashMap with all variables and their values
     * @return true if var name is reserved
     */
    private boolean isVarsContainsReservedValues(HashMap<String, Double> vars) {
        return vars.containsKey("sin") || vars.containsKey("cos") || vars.containsKey("tg") || vars.containsKey("atg")
                || vars.containsKey("sqrt") || vars.containsKey("log") || vars.containsKey("ctg");
    }

    /**
     * Method that replace all variables in formula with equivalent of their value AND right representation of operator
     * So "1-a" with a = -3 will be equal "1+3"
     *
     * @return fully working formula, with only numbers, without their variable representation
     */
    public String getCompletedFormula( ) {
        String formula = getFormula();
        HashMap<String, Double> vars = this.getArguments();
        String variablePresentation;
        for (String key : vars.keySet()) {
            /*
            this "\\b" - means that replacing will affect only letter
            like: "a" will be replaced with "a", and "aa" will only be replaced with "aa",
            not with "a" two times
             */
            if (!vars.isEmpty() && formula.contains(key)) {
                variablePresentation = "\\b" + key + "\\b";
                if (vars.get(key) < 0 && formula.indexOf(key) != 0) {
                    if (formula.charAt(formula.indexOf(key) - 1) == '-') {
                        formula = formula.replaceAll("-" + variablePresentation, String.valueOf(-1 * vars.get(key)));
                    } else {
                        formula = formula.replaceAll(" \\+ " + variablePresentation, " - " + -1 * vars.get(key));
                    }
                } else {
                    formula = formula.replaceAll(variablePresentation, vars.get(key).toString());
                }
            } else {
                return formula;
            }
        }
        if (!formula.matches("[a-zA-Z]+")) {
            return formula;
        } else {
            throw new IllegalStateException("No variables found to use");
        }

    }

}
