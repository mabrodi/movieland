package org.dimchik.web.controller;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/movies")
    public ResponseEntity<List<MovieResponseDTO>> findAll(
            @RequestParam(defaultValue = "desc") String rating,
            @RequestParam(required = false) String price) {

        List<MovieResponseDTO> movies = movieService.findAll(rating, price);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("movie/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable long id) {
        MovieResponseDTO movieResponseDTO = movieService.findById(id);
        return new ResponseEntity<>(movieResponseDTO, HttpStatus.OK);
    }

    @GetMapping("movies/random")
    public ResponseEntity<List<MovieResponseDTO>> random() {
        List<MovieResponseDTO> movies = movieService.random(3);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/movie/genre/{genreId}")
    public ResponseEntity<List<MovieResponseDTO>> findByGenreId(@PathVariable long genreId) {
        List<MovieResponseDTO> movies = movieService.findByGenreId(genreId);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
