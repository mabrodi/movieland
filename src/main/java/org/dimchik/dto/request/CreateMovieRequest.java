package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMovieRequest {
    @Schema(description = "${swagger.dto.create-movie.name-russian}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Russian name is required")
    private String nameRussian;

    @Schema(description = "${swagger.dto.create-movie.name-native}")
    @NotBlank(message = "Native name is required")
    private String nameNative;

    @Schema(description = "${swagger.dto.create-movie.year-of-release}")
    @Min(value = 1888, message = "Year must be realistic")
    private int yearOfRelease;

    @Schema(description = "${swagger.dto.create-movie.description}")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "${swagger.dto.create-movie.price}")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private double price;

    @Schema(description = "${swagger.dto.create-movie.rating}")
    @DecimalMin(value = "0.0", message = "Rating must be 0 or more")
    @DecimalMax(value = "10.0", message = "Rating cannot exceed 10")
    private double rating;

    @Schema(description = "${swagger.dto.create-movie.picture-path}")
    @NotBlank(message = "Picture path is required")
    private String picturePath;

    @Schema(description = "${swagger.dto.create-movie.countries}")
    @NotEmpty(message = "At least one country is required")
    private List<Long> countries;

    @Schema(description = "${swagger.dto.create-movie.genres}")
    @NotEmpty(message = "At least one genre is required")
    private List<Long> genres;
}
