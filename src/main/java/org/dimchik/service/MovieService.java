package org.dimchik.service;

import org.dimchik.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {
    List<MovieResponseDTO> findAll(String rating, String price);

    List<MovieResponseDTO> random(int count);

    MovieResponseDTO findById(long id);

    List<MovieResponseDTO> findByGenreId(long genreId);
}
