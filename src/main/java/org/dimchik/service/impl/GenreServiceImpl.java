package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.response.GenreResponse;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.repository.GenreRepository;
import org.dimchik.service.GenreService;
import org.dimchik.mapper.GenreMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<GenreResponse> findAll() {
        return genreMapper.toResponseList(genreRepository.findAll());
    }

    @Override
    public void enrichSingleMovieByGenres(Movie movie) {
        log.info("Start to enrich single movie by genres");

        List<Genre> genres = genreRepository.findAllByMovieId(movie.getId());

        if (!Thread.currentThread().isInterrupted()) {
            movie.setGenres(genres);
            log.info("Finish to enrich single movie by genres");
        }
    }

    @Override
    public List<Genre> findAllIds(List<Long> ids) {
        log.info("start give list genres by ids : {}", ids);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Genre> genres = genreRepository.findAllById(ids);

        if (ids.size() != genres.size()) {
            throw new IllegalArgumentException("Some genres not found");
        }

        log.info("end give list genres by ids: {}", ids);

        return genres;
    }
}
