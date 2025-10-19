package org.dimchik.common.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMovieRequest {
    @NotBlank(message = "Russian name is required")
    private String nameRussian;

    @NotBlank(message = "Native name is required")
    private String nameNative;

    @Min(value = 1888, message = "Year must be realistic")
    private int yearOfRelease;

    @NotBlank(message = "Description is required")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private double price;

    @DecimalMin(value = "0.0", message = "Rating must be 0 or more")
    @DecimalMax(value = "10.0", message = "Rating cannot exceed 10")
    private double rating;

    @NotBlank(message = "Picture path is required")
    private String picturePath;

    @NotEmpty(message = "At least one country is required")
    private List<Long> countries;

    @NotEmpty(message = "At least one genre is required")
    private List<Long> genres;
}
