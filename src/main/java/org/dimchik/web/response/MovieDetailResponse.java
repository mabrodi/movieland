package org.dimchik.web.response;

import lombok.*;
import org.dimchik.dto.GenreDTO;
import org.dimchik.dto.ReviewDTO;
import org.dimchik.dto.CountryDTO;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailResponse {
    private long id;
    private String nameRussian;
    private String nameNative;
    private int yearOfRelease;
    private double rating;
    private double price;
    private String description;
    private String picturePath;
    private List<CountryDTO> countries;
    private List<GenreDTO> genres;
    private List<ReviewDTO> reviews;
}
