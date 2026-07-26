package org.dimchik.exception;

public class AlreadyRatedException extends RuntimeException {
    public AlreadyRatedException(String message) {
        super(message);
    }
}
