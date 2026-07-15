package org.dimchik.mapper;

import org.dimchik.dto.response.*;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {
    @Mapping(target = "picturePath", source = "poster.picturePath")
    List<MovieResponse> toResponseList(List<Movie> movie);

    @Mapping(target = "picturePath", source = "poster.picturePath")
    MovieDetailResponse toDetailResponse(Movie movie);

    GenreResponse toGenreResponse(Genre genre);

    CountryResponse toCountryResponse(Country country);

    ReviewResponse toReviewResponse(Review review);

    @Mapping(target = "poster", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "countries", ignore = true)
    Movie createMovieFromEntity(CreateMovieRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "poster", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "countries", ignore = true)
    void updateMovieFromRequest(UpdateMovieRequest request, @MappingTarget Movie movie);
}
