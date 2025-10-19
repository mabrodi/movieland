package org.dimchik.common.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    @NotBlank
    private long movieId;
    @NotBlank
    private String text;
}
