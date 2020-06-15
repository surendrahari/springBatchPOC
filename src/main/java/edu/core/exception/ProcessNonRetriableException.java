package edu.core.exception;

public class ProcessNonRetriableException extends NonRetriableException {

    public ProcessNonRetriableException(String message) {
        super(message);
    }

    public ProcessNonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
