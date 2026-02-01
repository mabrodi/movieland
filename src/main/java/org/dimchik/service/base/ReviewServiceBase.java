package org.dimchik.service.base;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.ReviewDTO;
import org.dimchik.dto.UserDTO;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.entity.User;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.ReviewRepository;
import org.dimchik.repository.UserRepository;
import org.dimchik.service.ReviewService;
import org.dimchik.web.request.CreateReviewRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceBase implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;


    @Override
    public ReviewDTO create(CreateReviewRequest request, UserDetails userDetails) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + request.getMovieId()));
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userDetails.getUsername()));
        Review review = new Review();
        review.setMovie(movie);
        review.setAuthor(user);
        review.setComment(request.getText());
        Review createdReview = reviewRepository.save(review);

        UserDTO userDTO = new UserDTO(review.getAuthor().getId(), review.getAuthor().getName());

        return new ReviewDTO(createdReview.getId(), createdReview.getComment(), userDTO);
    }
}
