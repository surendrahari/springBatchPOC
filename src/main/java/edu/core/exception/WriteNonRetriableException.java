package edu.core.exception;

public class WriteNonRetriableException extends NonRetriableException {

    public WriteNonRetriableException(String message) {
        super(message);
    }

    public WriteNonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
