package org.dimchik.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
    @Schema(description = "Unique country identifier")
    private long id;

    @Schema(description = "Country name")
    private String name;
}
