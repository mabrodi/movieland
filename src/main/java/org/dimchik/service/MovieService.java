package org.dimchik.service;

import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.MovieByIdRequest;
import org.dimchik.common.request.MovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;

import java.util.List;

public interface MovieService {
    List<MovieDTO> findAll(MovieRequest request);

    List<MovieDTO> random(int count);

    MovieFullDTO findById(long id, MovieByIdRequest request);

    List<MovieDTO> findByGenreId(long genreId);

    MovieFullDTO create(CreateMovieRequest request);

    MovieFullDTO update(long id, UpdateMovieRequest request);
}
