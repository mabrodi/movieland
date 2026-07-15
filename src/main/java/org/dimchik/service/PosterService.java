package org.dimchik.service;

import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;

public interface PosterService {
    Poster upsertPoster(Movie movie, String picturePath);
}
