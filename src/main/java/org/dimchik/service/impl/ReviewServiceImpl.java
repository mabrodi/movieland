package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.dto.response.UserResponse;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.entity.User;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.ReviewRepository;
import org.dimchik.repository.UserRepository;
import org.dimchik.service.ReviewService;
import org.dimchik.exception.MovieNotFoundException;
import org.dimchik.exception.UserNotFoundException;
import org.dimchik.dto.request.CreateReviewRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse create(CreateReviewRequest request, JwtUserDetails userDetails) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(request.getMovieId()));
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getEmail()));

        Review review = new Review();
        review.setMovie(movie);
        review.setAuthor(user);
        review.setComment(request.getText());

        Review createdReview = reviewRepository.save(review);
        UserResponse userResponse = new UserResponse(review.getAuthor().getId(), review.getAuthor().getName());

        return new ReviewResponse(createdReview.getId(), createdReview.getComment(), userResponse);
    }

    @Override
    public void enrichSingleMovieByReviewes(Movie movie) {
        log.info("Start to enrich single movie by reviews");

        List<Review> reviews = reviewRepository.findAllByMovieId(movie.getId());

        if (!Thread.currentThread().isInterrupted()) {
            movie.setReviews(reviews);
            log.info("Finish to enrich single movie by reviews");
        }
    }
}
