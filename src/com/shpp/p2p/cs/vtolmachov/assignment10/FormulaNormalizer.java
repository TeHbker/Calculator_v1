package com.shpp.p2p.cs.vtolmachov.assignment10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FormulaNormalizer {
    private final String[] arg;
    private static final String[] COMBINATIONS_OF_SYMBOLS = {"++", "--", "+-", "-+"};
    public static final String[] MAIN_OPERATORS = {"+", "-", "*", "/", "^", "(", ")"};

    public FormulaNormalizer(String[] args) {
        arg = removeSpaces(args);
    }

    private String[] removeSpaces(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll(" ", "");
        }

        return args;
    }

    private String getFormula() {
        String formula = this.arg[0];
        return formula;
    }

    public HashMap<String, Double> getArguments() {
        HashMap<String, Double> arguments = new HashMap<>();
        for (int i = 1; i < arg.length; i++) {
            String[] a = arg[i].split("=");
            arguments.put(a[0], Double.parseDouble(a[1]));
        }
        return arguments;
    }

    public ArrayList<String> getFullFormula() {
        String sFormula = getFormula();
        sFormula = replaceVarsWithValues(sFormula);
        String symbolToReplace = "";
        sFormula = replaceAllDoubleOperators(symbolToReplace, sFormula);
        sFormula = makeSpacesBetweenOperators(sFormula);
        return new ArrayList<>(Arrays.asList(sFormula.split(" ")));
    }

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

    private static String specialConditionsForMinus(String sFormula) {
        for(int i = 1; i < sFormula.length(); i++) {
            if(sFormula.charAt(i) == '-' && String.valueOf(sFormula.charAt(i - 1)).matches("\\d+")) {
                sFormula = sFormula.substring(0, i) + " " + "-" + " " +  sFormula.substring(i + 1);
            }
        }
        return sFormula;
    }

    private String replaceAllDoubleOperators(String symbolToReplace, String sFormula) {
        for (String combinationsOfSymbol : COMBINATIONS_OF_SYMBOLS) {
            symbolToReplace = switch (combinationsOfSymbol) {
                case "++", "--" -> "+";
                case "+-", "-+" -> "-";
                default -> symbolToReplace;
            };
            sFormula = sFormula.replace(combinationsOfSymbol, symbolToReplace);
        }
        return sFormula;
    }

    private String replaceVarsWithValues(String sFormula) {
        HashMap<String, Double> arguments = getArguments();
        for (String key : arguments.keySet()) {
            sFormula = sFormula.replaceAll(key, String.valueOf(arguments.get(key)));
        }
        return sFormula;
    }


}
