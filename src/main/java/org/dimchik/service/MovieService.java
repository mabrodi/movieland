package org.dimchik.service;

import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.MovieByIdRequest;
import org.dimchik.web.request.MovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;

import java.util.List;

public interface MovieService {
    public List<MovieDTO> findAll(MovieRequest request);

    public MovieFullDTO findById(long id, MovieByIdRequest request);

    public List<MovieDTO> random(int count);

    public List<MovieDTO> findByGenreId(long genreId);

    public MovieFullDTO create(CreateMovieRequest request);

    public MovieFullDTO update(long id, UpdateMovieRequest request);
}
