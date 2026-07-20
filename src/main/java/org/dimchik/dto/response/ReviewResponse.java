package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    @Schema(description = "Unique review identifier")
    private long id;

    @Schema(description = "Review text content")
    private String comment;

    @Schema(description = "Information about the review author")
    private UserResponse user;
}
