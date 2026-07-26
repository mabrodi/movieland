package org.dimchik.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.request.MovieRatingRequest;
import org.dimchik.dto.response.MovieRatingResponse;
import org.dimchik.service.MovieRatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name="${swagger.movie-ratings.tag.name}", description = "${swagger.movie-ratings.tag.description}")
public class MovieRatingController {
    private final MovieRatingService movieRatingService;

    @Operation(
            summary = "${swagger.movie-ratings.find-by-movie-id.summary}",
            description = "${swagger.movie-ratings.find-by-movie-id.description}"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/{movieId}/rate")
    public MovieRatingResponse findByMovieId(
            @Parameter(description = "${swagger.movie-ratings.find-by-movie-id.param-movie-id}", example = "1")
            @PathVariable long movieId,
            JwtUserDetails userDetails) {
        return movieRatingService.findByMovieIdAndUser(movieId, userDetails);
    }

    @Operation(
            summary = "${swagger.movie-ratings.create.summary}",
            description = "${swagger.movie-ratings.create.description}"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/{movieId}/rate")
    public MovieRatingResponse create(
            @Parameter(description = "${swagger.movie-ratings.create.param-movie-id}", example = "1")
            @PathVariable long movieId,
            @Valid @RequestBody MovieRatingRequest movieRatingRequest,
            JwtUserDetails userDetails
    ) {
        return movieRatingService.create(movieId, movieRatingRequest.getRating(), userDetails);
    }

    @Operation(
            summary = "${swagger.movie-ratings.update.summary}",
            description = "${swagger.movie-ratings.update.description}"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PutMapping("/{movieId}/rate")
    public MovieRatingResponse update(
            @Parameter(description = "${swagger.movie-ratings.update.param-movie-id}", example = "1")
            @PathVariable long movieId,
            @Valid @RequestBody MovieRatingRequest movieRatingRequest,
            JwtUserDetails userDetails
    ) {
        return movieRatingService.create(movieId, movieRatingRequest.getRating(), userDetails);
    }
}
