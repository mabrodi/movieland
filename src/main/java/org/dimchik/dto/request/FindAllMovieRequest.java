package org.dimchik.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dimchik.enums.SortDirection;

@Setter
@Getter
public class FindAllMovieRequest {
    @Schema(description = "${swagger.dto.find-all-movie.rating-sort-direction}", example = "DESC")
    private SortDirection ratingSortDirection;

    @Schema(description = "${swagger.dto.find-all-movie.price-sort-direction}", example = "ASC")
    private SortDirection priceSortDirection;
}
