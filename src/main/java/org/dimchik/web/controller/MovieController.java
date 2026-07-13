package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.MovieService;
import org.dimchik.service.mapper.MovieMapper;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.MovieByIdRequest;
import org.dimchik.dto.request.MovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final MovieMapper movieMapper;
    private final CurrencyService currencyService;

    @GetMapping("/movies")
    public List<MovieResponse> findAll(@Valid @ModelAttribute MovieRequest request) {
        return movieService.findAll(request.getRatingSortDirection(), request.getPriceSortDirection());
    }

    @GetMapping("/movies/{id}")
    public MovieDetailResponse findById(@PathVariable long id, @ModelAttribute MovieByIdRequest request) {
        MovieDetailResponse movieDetailResponse = movieService.findById(id);
        currencyService.convertPriceInMovieDetailResponse(movieDetailResponse, request.getCurrency());
        return movieDetailResponse;
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
