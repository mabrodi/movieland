package org.dimchik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RateDTO {
    @JsonProperty("r030")
    private long id;

    @JsonProperty("txt")
    private String name;

    @JsonProperty("cc")
    private String currency;

    @JsonProperty("rate")
    private double rate;
}
