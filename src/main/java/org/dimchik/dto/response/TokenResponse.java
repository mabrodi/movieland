package org.dimchik.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String token;
    private String tokenType = "Bearer";
}
