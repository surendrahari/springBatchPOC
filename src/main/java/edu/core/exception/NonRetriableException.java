package edu.core.exception;

public class NonRetriableException extends Exception {

    public NonRetriableException(String message) {
        super(message);
    }

    public NonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
