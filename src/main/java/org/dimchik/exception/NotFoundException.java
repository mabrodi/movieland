package org.dimchik.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}
