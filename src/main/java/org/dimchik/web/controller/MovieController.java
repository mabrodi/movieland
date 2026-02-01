package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.service.MovieService;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.MovieByIdRequest;
import org.dimchik.web.request.MovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/movies")
    public List<MovieDTO> findAll(@Valid @ModelAttribute MovieRequest request) {
        return movieService.findAll(request);
    }

    @GetMapping("movie/{id}")
    public MovieFullDTO findById(@PathVariable long id, @ModelAttribute MovieByIdRequest request) {
        return movieService.findById(id, request);
    }

    @GetMapping("movies/random")
    public List<MovieDTO> random() {
        return movieService.random(3);
    }

    @GetMapping("/movie/genre/{genreId}")
    public List<MovieDTO> findByGenreId(@PathVariable long genreId) {
        return movieService.findByGenreId(genreId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/movie")
    public MovieFullDTO create(@Valid @RequestBody CreateMovieRequest request) {
        return movieService.create(request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("movie/{id}")
    public MovieFullDTO update(@PathVariable long id, @Valid @RequestBody UpdateMovieRequest request) {
        return movieService.update(id, request);
    }
}
