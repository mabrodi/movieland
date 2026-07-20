package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @Schema(description = "${swagger.dto.login.email}")
    @Email(message = "Email should be valid")
    @NotBlank
    private String email;

    @Schema(description = "${swagger.dto.login.password}")
    @NotBlank
    private String password;
}
