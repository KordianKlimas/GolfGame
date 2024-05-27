package com.kentwentyfour.project12;

import java.util.*;

/**
 * Set of often used methods.
 * <br>
 * To use: import static com.example.gui.Utilities.*;
 */
public final class Utilities {
    // checks if the string is a number
    /**
     * This method checks if the string is a number.
     * <br>
     * Usage: {@code boolean result = Utilities.isNumeric(str);}
     *
     * @param str The string to check
     * @return {@code true} if the string is numeric, {@code false} otherwise
     * @author Kordian
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * This method safely converts a string to a double.
     * <br>
     * Usage: {@code double result = Utilities.strToDouble(str);}
     *
     * @param str The string to convert to double
     * @return The converted double value, or 0.0 if conversion fail
     * @author Kordian
     */
    public static  double strToDouble(String str) {
        try {
            return  Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.out.println("error strToDouble failed to convert: " + str);
            return  0.0;
        }
    }
    public static String extractSubstringAfterGivenString(String input, String givenSting) {
        int index = input.indexOf(givenSting);
        if (index != -1 && index + 1 < input.length()) {
            return input.substring(index + 1, index + 2);
        } else {
            return "";
        }
    }


}
