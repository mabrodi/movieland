package org.dimchik.web.exception;

public class MovieNotFoundException extends ResourceNotFoundException {
    public MovieNotFoundException(long id) {
        super("Movie not found with id: " + id);
    }
}
