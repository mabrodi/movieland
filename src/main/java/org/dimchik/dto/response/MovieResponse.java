package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    @Schema(description = "Unique movie identifier")
    private long id;

    @Schema(description = "Movie name in Russian")
    private String nameRussian;

    @Schema(description = "Movie name in original language")
    private String nameNative;

    @Schema(description = "Year the movie was released")
    private int yearOfRelease;

    @Schema(description = "Average movie rating (0.0 - 10.0)")
    private double rating;

    @Schema(description = "Movie price in default currency")
    private double price;

    @Schema(description = "Path to the movie poster image")
    private String picturePath;
}
