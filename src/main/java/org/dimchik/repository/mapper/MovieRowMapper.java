package org.dimchik.repository.mapper;

import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.*;
import org.dimchik.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieRowMapper {
    public MovieDTO convertToDTO(Movie movie) {
        MovieDTO movieResponseDTO = new MovieDTO();
        movieResponseDTO.setId(movie.getId());
        movieResponseDTO.setNameRussian(movie.getNameRussian());
        movieResponseDTO.setNameNative(movie.getNameNative());
        movieResponseDTO.setYearOfRelease(movie.getYearOfRelease());
        movieResponseDTO.setRating(movie.getRating());
        movieResponseDTO.setPrice(movie.getPrice());
        movieResponseDTO.setPicturePath(movie.getPoster().getPicturePath());

        return movieResponseDTO;
    }

    public MovieFullDTO convertToFullDTO(Movie movie) {
        MovieFullDTO movieFullDTO = new MovieFullDTO();
        movieFullDTO.setId(movie.getId());
        movieFullDTO.setNameRussian(movie.getNameRussian());
        movieFullDTO.setNameNative(movie.getNameNative());
        movieFullDTO.setYearOfRelease(movie.getYearOfRelease());
        movieFullDTO.setRating(movie.getRating());
        movieFullDTO.setPrice(movie.getPrice());
        movieFullDTO.setPicturePath(movie.getPoster().getPicturePath());
        movieFullDTO.setCountries(movie.getCountries().stream()
                .map(country -> new CountryDTO(country.getId(), country.getName()))
                .toList());
        movieFullDTO.setGenres(movie.getGenres().stream()
                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                .toList());
        movieFullDTO.setReviews(movie.getReviews().stream()
                .map(review -> new ReviewDTO(
                        review.getId(),
                        review.getComment(),
                        new UserDTO(review.getAuthor().getId(), review.getAuthor().getName())))
                .toList()
        );


        return movieFullDTO;
    }

    public Movie convertCreateMovieRequestToMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        movie.setNameRussian(request.getNameRussian());
        movie.setNameNative(request.getNameNative());
        movie.setDescription(request.getDescription());
        movie.setYearOfRelease(request.getYearOfRelease());
        movie.setPrice(request.getPrice());
        movie.setRating(request.getRating());

        return movie;
    }
}
