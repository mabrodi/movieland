package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieRatingResponse {
    @Schema(description = "Unique movie identifier")
    private long movieId;

    @Schema(description = "Rating value from 0.0 to 10.0")
    private double rating;

    @Schema(description = "Unique user identifier who submitted the rating")
    private long userId;
}
