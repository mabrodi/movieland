package org.dimchik.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.PosterRepository;
import org.dimchik.service.PosterService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PosterServiceImpl implements PosterService {
    private final PosterRepository posterRepository;

    @Override
    public void upsertPoster(Movie movie, String picturePath) {
        Poster poster = movie.getPoster();
        if (poster == null) {
            poster = new Poster();
            poster.setMovie(movie);
        }
        poster.setPicturePath(picturePath);
        posterRepository.save(poster);
    }
}
