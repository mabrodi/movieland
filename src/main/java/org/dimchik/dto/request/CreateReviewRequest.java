package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    @Schema(description = "${swagger.dto.create-review.movie-id}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private long movieId;

    @Schema(description = "${swagger.dto.create-review.text}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String text;
}
