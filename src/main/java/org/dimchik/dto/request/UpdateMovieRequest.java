package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateMovieRequest {
    @Schema(description = "${swagger.dto.create-movie.name-russian}")
    private String nameRussian;

    @Schema(description = "${swagger.dto.create-movie.name-native}")
    private String nameNative;

    @Schema(description = "${swagger.dto.create-movie.picture-path}")
    private String picturePath;

    @Schema(description = "${swagger.dto.create-movie.countries}")
    private List<Long> countries;

    @Schema(description = "${swagger.dto.create-movie.genres}")
    private List<Long> genres;
}
