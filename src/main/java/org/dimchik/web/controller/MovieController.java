package org.dimchik.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.enums.Currency;
import org.dimchik.security.PublicEndpoint;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.MovieService;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.FindAllMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name="${swagger.movies.tag.name}", description = "${swagger.movies.tag.description}")
public class MovieController {
    private final MovieService movieService;
    private final CurrencyService currencyService;

    @PublicEndpoint
    @Operation(summary = "${swagger.movies.find-all.summary}", description = "${swagger.movies.find-all.description}")
    @GetMapping
    public List<MovieResponse> findAll(@Valid @ModelAttribute FindAllMovieRequest request) {
        return movieService.findAll(request);
    }

    @Operation(
            summary = "${swagger.movies.find-by-id.summary}",
            description = "${swagger.movies.find-by-id.description}"
    )
    @PublicEndpoint
    @GetMapping("/{id}")
    public MovieDetailResponse findById(
            @Parameter(description = "${swagger.movies.find-by-id.param-id}", example = "1")
            @PathVariable long id,
            @RequestParam(defaultValue = Currency.DEFAULT) Currency currency) {
        MovieDetailResponse movieDetailResponse = movieService.findById(id);
        currencyService.convertPriceInMovieDetailResponse(movieDetailResponse, currency);
        return movieDetailResponse;
    }

    @PublicEndpoint
    @Operation(summary = "${swagger.movies.random.summery}", description = "${swagger.movies.random.description}")
    @GetMapping("/random")
    public List<MovieResponse> random() {
        return movieService.random(3);
    }

    @PublicEndpoint
    @Operation(
            summary = "${swagger.movies.find-by-genre-id.summery}",
            description = "${swagger.movies.find-by-genre-id.description}"
    )
    @GetMapping("/genre/{genreId}")
    public List<MovieResponse> findByGenreId(
            @Parameter(description = "${swagger.movies.find-by-genre-id.param-genre-id}", example = "1")
            @PathVariable long genreId) {
        return movieService.findByGenreId(genreId);
    }

    @Operation(summary = "${swagger.movies.create.summary}", description = "${swagger.movies.create.description}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public MovieDetailResponse create(@Valid @RequestBody CreateMovieRequest request) {
        return movieService.create(request);
    }

    @Operation(summary = "${swagger.movies.update.summary}", description = "${swagger.movies.update.description}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public MovieDetailResponse update(
            @Parameter(description = "${swagger.movies.update.param-movie-id}", example = "1")
            @PathVariable long id,
            @Valid @RequestBody UpdateMovieRequest request) {
        return movieService.update(id, request);
    }

    @Operation(summary = "${swagger.movies.mark.summary}", description = "${swagger.movies.mark.description}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void mark(
            @Parameter(description = "${swagger.movies.mark.param-id}", example = "1")
            @PathVariable long id) {
        movieService.queueForDeletion(id);
    }

    @Operation(summary = "${swagger.movies.unmark.summary}", description = "${swagger.movies.unmark.description}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/{id}/unmark")
    public void unmark(
            @Parameter(description = "${swagger.movies.unmark.param-id}", example = "1")
            @PathVariable long id) {
        movieService.removeFromDeletionQueue(id);
    }
}
