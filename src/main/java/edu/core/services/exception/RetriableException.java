package edu.core.services.exception;

public class RetriableException extends Exception {

    public RetriableException(String message) {
        super(message);
    }
}
