package org.dimchik.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionDto {
    @Schema(description = "HTTP status code of the error")
    private HttpStatus errorCode;

    @Schema(description = "Human-readable error message")
    private String message;
}
