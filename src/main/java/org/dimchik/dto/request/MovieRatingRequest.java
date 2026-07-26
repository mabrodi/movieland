package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRatingRequest {
    @Schema(description = "${swagger.dto.movie-rating.rating}", example = "8.5")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private double rating;
}
