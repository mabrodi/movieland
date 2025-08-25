package org.dimchik.exception;

import org.dimchik.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDTO> handleApp(AppException e) {
        ErrorDTO errorDTO = new ErrorDTO(e.getCode(), e.getMessage());
        return ResponseEntity.status(e.getCode()).body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleOther(Exception e) {
        ErrorDTO error = new ErrorDTO(500, "Internal server error");
        return ResponseEntity.status(500).body(error);
    }
}
