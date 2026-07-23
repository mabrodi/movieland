package org.dimchik.service;

import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.dto.request.CreateReviewRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request, JwtUserDetails user);

    void enrichSingleMovieByReviewes(Movie movie);
}
