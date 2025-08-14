package com.shpp.p2p.cs.vtolmachov.assignment10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FormulaNormalizer {
    private final String[] FORMULA_AND_VARIABLES;
    private static final String[] COMBINATIONS_OF_SYMBOLS = {"++", "--", "+-", "-+"};
    public static final String[] MAIN_OPERATORS = {"+", "-", "*", "/", "^", "(", ")"};

    /**
     * Constructor for formula normalizer
     *
     * @param args incoming data that will be processing in next methods
     */
    public FormulaNormalizer(String[] args) {
        FORMULA_AND_VARIABLES = removeSpaces(args);
    }

    /**
     * Method that removes all spaces in formula and every variable so it will be easier to parse
     *
     * @param args raw formula and variables with spaces in random places
     * @return not separated formula and variables
     */
    private String[] removeSpaces(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll(" ", "");
        }

        return args;
    }

    /**
     * Method split variables from input args and turns it into easy to read HasMap
     *
     * @return HasMap of variables and their value
     */
    public HashMap<String, Double> getArguments() {
        HashMap<String, Double> arguments = new HashMap<>();
        for (int i = 1; i < FORMULA_AND_VARIABLES.length; i++) {
            String[] a = FORMULA_AND_VARIABLES[i].split("=");
            arguments.put(a[0], Double.parseDouble(a[1]));
        }
        return arguments;
    }

    /**
     * Method that makes all operations on raw formula to turn it into array of symbols which is easy to work with
     *
     * @return Array representation that contains all operators/numbers from formula
     */
    public ArrayList<String> getFullFormula() {
        String sFormula = this.FORMULA_AND_VARIABLES[0];
        sFormula = replaceVarsWithValues(sFormula);

        sFormula = replaceAllDoubleOperators(sFormula);
        sFormula = makeSpacesBetweenOperators(sFormula);
        return new ArrayList<>(Arrays.asList(sFormula.split(" ")));
    }

    /**
     * Method that replace all operators with themselves but surrounded with spaces
     *
     * @param sFormula Formula with only numbers, but not separated with spaces
     * @return Same formula with spaces between every operator/number
     */
    private static String makeSpacesBetweenOperators(String sFormula) {
        for (String operator : MAIN_OPERATORS) {
            switch (operator) {
                case "-" -> sFormula = specialConditionsForMinus(sFormula);
                case "(" -> sFormula = sFormula.replace(operator, "( ");
                case ")" -> sFormula = sFormula.replace(operator, " )");
                default -> sFormula = sFormula.replace(operator, " " + operator + " ");
            }
        }
        return sFormula;
    }

    /**
     * Method that contains special rules to replace operator minus. Because not every minus are subject for
     * certain rules. Like first minus shouldn't have pattern for replacement like " " + "-" + " "
     * Or minus could be part of number not an operator
     * Variants of replacement:
     * 3-4 -> 3 - 4
     * -(2+3) -> -1 * (2+3)
     *
     * @param sFormula raw formula with replaced variables on their number equivalent
     * @return formula with correctly replaced operator "-"
     */
    private static String specialConditionsForMinus(String sFormula) {
        /*
          So, this case is for FIRST minus before number and/or brackets. Because minus is not Math function
          by itself, this part resolve it and replaces common "-(*Some formula*)" into "-1 * (Some formula)"
         */
        if (sFormula.charAt(0) == '-') {
            sFormula = sFormula.charAt(0) + "1*" + sFormula.substring(1);
        }

        for (int i = 1; i < sFormula.length(); i++) {
            if (sFormula.charAt(i) == '-' && String.valueOf(sFormula.charAt(i - 1)).matches("\\d+")) {
                sFormula = sFormula.substring(0, i) + " " + "-" + " " + sFormula.substring(i + 1);
            }
        }
        return sFormula;
    }

    /**
     * Method that check formula and replaces all common and legit variants of double-symbols
     *
     * @param sFormula Formula with possible double-symbols that comes after variable replacement
     * @return Correctly completed formula without spaces, variables and double-operators
     */
    private String replaceAllDoubleOperators(String sFormula) {
        for (String combinationsOfSymbol : COMBINATIONS_OF_SYMBOLS) {
            sFormula = sFormula.replace(combinationsOfSymbol, symbolToReplace(combinationsOfSymbol));
        }
        return sFormula;
    }

    /**
     * Method that check formula and replaces all common and legit variants of double-symbols
     *
     * @param combinationsOfSymbol One of possible and legit combination of symbols
     * @return replacement for this double-symbol with one symbol
     */
    private static String symbolToReplace(String combinationsOfSymbol) {
        return switch (combinationsOfSymbol) {
            case "++", "--" -> "+";
            case "+-", "-+" -> "-";
            default -> "";
        };
    }

    /**
     * Method that replaces all variables in formula with their number equivalent
     *
     * @param sFormula Raw formula without spaces and with letters(as variables)
     * @return Formula without spaces but with variables replaced by numbers
     * TODO: Add Try-catch to resolve problem з відсутністю чисельного еквіваленту змінній
     */
    private String replaceVarsWithValues(String sFormula) {
        HashMap<String, Double> arguments = getArguments();
        for (String key : arguments.keySet()) {
            sFormula = sFormula.replaceAll(key, String.valueOf(arguments.get(key)));
        }
        return sFormula;
    }


}
