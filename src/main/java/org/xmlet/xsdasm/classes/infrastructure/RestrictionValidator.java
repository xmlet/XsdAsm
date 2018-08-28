package org.xmlet.xsdasm.classes.infrastructure;

import java.util.List;

public class RestrictionValidator {

    private RestrictionValidator() {}

    public static void validateFractionDigits(int fractionDigits, double value){
        if (value != ((int) value)){
            String doubleValue = String.valueOf(value);

            int numberOfFractionDigits = doubleValue.substring(doubleValue.indexOf(",")).length();

            if (numberOfFractionDigits > fractionDigits){
                throw new RestrictionViolationException("Violation of fractionDigits restriction, value should have a maximum of " + fractionDigits + " decimal places.");
            }
        }
    }

    public static void validateLength(int length, String string){
        if (string.length() != length){
            throw new RestrictionViolationException("Violation of length restriction, string should have exactly " + length + " characters.");
        }
    }

    public static void validateLength(int length, List list){
        if (list.size() != length){
            throw new RestrictionViolationException("Violation of length restriction, list should have exactly " + length + " elements.");
        }
    }

    public static void validateMaxExclusive(double maxExclusive, double value){
        if (value >= maxExclusive){
            throw new RestrictionViolationException("Violation of maxExclusive restriction, value should be lesser than " + maxExclusive);
        }
    }

    public static void validateMaxInclusive(double maxInclusive, double value){
        if (value > maxInclusive){
            throw new RestrictionViolationException("Violation of maxInclusive restriction, value should be lesser or equal to " + maxInclusive);
        }
    }

    public static void validateMaxLength(int maxLength, String string){
        if (string.length() > maxLength){
            throw new RestrictionViolationException("Violation of maxLength restriction, string should have a max number of characters of " + maxLength);
        }
    }

    public static void validateMaxLength(int maxLength, List list){
        if (list.size() > maxLength){
            throw new RestrictionViolationException("Violation of maxLength restriction, list should have a max number of items of " + maxLength);
        }
    }

    public static void validateMinExclusive(double minExclusive, double value){
        if (value <= minExclusive){
            throw new RestrictionViolationException("Violation of minExclusive restriction, value should be greater than " + minExclusive);
        }
    }

    public static void validateMinInclusive(double minInclusive, double value){
        if (value < minInclusive){
            throw new RestrictionViolationException("Violation of minInclusive restriction, value should be greater or equal to " + minInclusive);
        }
    }

    public static void validateMinLength(int minLength, String string){
        if (string.length() < minLength){
            throw new RestrictionViolationException("Violation of minLength restriction, string should have a minimum number of characters of " + minLength);
        }
    }

    public static void validateMinLength(int minLength, List list){
        if (list.size() < minLength){
            throw new RestrictionViolationException("Violation of minLength restriction, list should have a minimum number of items of " + minLength);
        }
    }

    public static void validatePattern(String pattern, String string){
        if (!string.replaceAll(pattern, "").equals(string)){
            throw new RestrictionViolationException("Violation of pattern restriction, the string doesn't math the acceptable pattern, which is " + pattern);
        }
    }

    public static void validateTotalDigits(int totalDigits, double value){
        String doubleValue = String.valueOf(value);

        int numberOfDigits;

        if (value != ((int) value)){
            numberOfDigits = doubleValue.length() - 1;
        } else {
            numberOfDigits = doubleValue.length();
        }

        if (numberOfDigits != totalDigits){
            throw new RestrictionViolationException("Violation of fractionDigits restriction, value should have a exactly " + totalDigits + " decimal places.");
        }
    }
}
