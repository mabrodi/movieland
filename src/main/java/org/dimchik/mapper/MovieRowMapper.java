package org.dimchik.mapper;

import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieRowMapper {
    public MovieResponseDTO convertToDTO(Movie movie) {
        MovieResponseDTO movieResponseDTO = new MovieResponseDTO();
        movieResponseDTO.setId(movie.getId());
        movieResponseDTO.setNameRussian(movie.getNameRussian());
        movieResponseDTO.setNameNative(movie.getNameNative());
        movieResponseDTO.setYearOfRelease(movie.getYearOfRelease());
        movieResponseDTO.setRating(movie.getRating());
        movieResponseDTO.setPrice(movie.getPrice());
        movieResponseDTO.setPicturePath(movie.getPoster().getPicturePath());

        return movieResponseDTO;
    }
}
