package org.dimchik.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.dimchik.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.FORBIDDEN, "Access Denied");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fe ? fe.getField() : "unknown";
            String errorMessage = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
            errors.put(fieldName, errorMessage);
        });

        return new ExceptionDto(HttpStatus.BAD_REQUEST, errors.toString());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDto handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDto handleTokenInvalid(TokenInvalidException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(RelatedEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleRelatedEntityNotFound(RelatedEntityNotFoundException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleGenericException(Exception e, HttpServletRequest request) {
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
