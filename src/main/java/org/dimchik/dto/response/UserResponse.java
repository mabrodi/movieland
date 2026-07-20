package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @Schema(description = "Unique user identifier")
    private long id;

    @Schema(description = "User display name")
    private String name;
}
