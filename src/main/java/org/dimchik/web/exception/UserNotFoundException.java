package org.dimchik.web.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier);
    }
}
