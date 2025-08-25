package org.dimchik.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {
    private final int code;

    public AppException(String message, int code) {
        super(message);
        this.code = code;
    }


}
