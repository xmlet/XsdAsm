package org.xmlet.xsdasm.classes.infrastructure;

/**
 * This exception is thrown whenever a Restriction is violated in any of the method defined in the {@link RestrictionValidator}
 * class. The {@link RestrictionViolationException#detailMessage} contains information to identify the violated restriction.
 */
public class RestrictionViolationException extends RuntimeException {

    public RestrictionViolationException(String message){
        super(message);
    }

}
