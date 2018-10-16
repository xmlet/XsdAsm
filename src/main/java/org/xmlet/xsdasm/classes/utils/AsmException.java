package org.xmlet.xsdasm.classes.utils;

/**
 * Represents a fatal error that occurred during the class generating process. The {@link AsmException#detailMessage}
 * contains details about the nature of the problem.
 */
public class AsmException extends RuntimeException {

    public AsmException(String message){
        super(message);
    }

    public AsmException(String message, Throwable exception){
        super(message, exception);
    }
}
