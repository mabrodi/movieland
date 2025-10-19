package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.MovieByIdRequest;
import org.dimchik.common.request.MovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.service.MovieService;
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

    @PostMapping("/movie")
    public MovieFullDTO create(@Valid @RequestBody CreateMovieRequest request) {
        return movieService.create(request);
    }

    @PutMapping("movie/{id}")
    public MovieFullDTO update(@PathVariable long id, @Valid @RequestBody UpdateMovieRequest request) {
        return movieService.update(id, request);
    }
}
