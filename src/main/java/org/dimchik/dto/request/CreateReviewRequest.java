package org.dimchik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    @Positive
    private long movieId;
    @NotBlank
    private String text;
}
