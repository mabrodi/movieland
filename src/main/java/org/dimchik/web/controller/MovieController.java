package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.web.response.MovieDetailResponse;
import org.dimchik.web.response.MovieResponse;
import org.dimchik.service.MovieService;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.MovieByIdRequest;
import org.dimchik.web.request.MovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/movies")
    public List<MovieResponse> findAll(@Valid @ModelAttribute MovieRequest request) {
        return movieService.findAll(request.getRatingSortDirection(), request.getPriceSortDirection());
    }

    @GetMapping("/movies/{id}")
    public MovieDetailResponse findById(@PathVariable long id, @ModelAttribute MovieByIdRequest request) {
        return movieService.findById(id, request.getCurrency());
    }

    @GetMapping("/movies/random")
    public List<MovieResponse> random() {
        return movieService.random(3);
    }

    @GetMapping("/movies/genre/{genreId}")
    public List<MovieResponse> findByGenreId(@PathVariable long genreId) {
        return movieService.findByGenreId(genreId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/movies")
    public MovieDetailResponse create(@Valid @RequestBody CreateMovieRequest request) {
        return movieService.create(request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/movies/{id}")
    public MovieDetailResponse update(@PathVariable long id, @Valid @RequestBody UpdateMovieRequest request) {
        return movieService.update(id, request);
    }
}
