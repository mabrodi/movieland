package org.dimchik.service;

import org.dimchik.entity.Movie;

public interface PosterService {
    void upsertPoster(Movie movie, String picturePath);
}
