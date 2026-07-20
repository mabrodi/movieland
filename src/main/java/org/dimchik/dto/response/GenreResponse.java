package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponse {
    @Schema(description = "Unique genre identifier")
    private long id;

    @Schema(description = "Genre name")
    private String name;
}
