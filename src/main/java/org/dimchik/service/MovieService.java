package org.dimchik.service;

import org.dimchik.dto.MovieResponseDTO;

import java.util.HashMap;
import java.util.List;

public interface MovieService {
    List<MovieResponseDTO> findAll(HashMap<String, String> filter);

    List<MovieResponseDTO> random(int count);

    MovieResponseDTO findById(long id);

    List<MovieResponseDTO> findByGenreId(long genreId);
}
