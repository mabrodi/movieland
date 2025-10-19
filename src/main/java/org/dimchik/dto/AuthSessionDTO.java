package org.dimchik.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthSessionDTO {
    private String token;
    private UserAuthDTO user;
    private LocalDateTime createdAt;
}
