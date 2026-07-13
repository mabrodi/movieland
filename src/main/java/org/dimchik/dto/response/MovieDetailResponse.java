package org.dimchik.dto.response;

import lombok.*;

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
    private List<CountryResponse> countries;
    private List<GenreResponse> genres;
    private List<ReviewResponse> reviews;
}
