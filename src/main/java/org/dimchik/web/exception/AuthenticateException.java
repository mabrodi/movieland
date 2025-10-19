package org.dimchik.web.exception;

public class AuthenticateException extends RuntimeException {
    public AuthenticateException(String message) {
        super(message);
    }
}
