package org.dimchik.web.controller;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> findAll(
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String price) {
        HashMap<String, String> filter = new HashMap<>();
        if (rating != null) {
            filter.put("rating", rating);
        }
        if (price != null) {
            filter.put("price", price);
        }
        List<MovieResponseDTO> movies = movieService.findAll(filter);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable long id) {
        MovieResponseDTO movieResponseDTO = movieService.findById(id);
        return new ResponseEntity<>(movieResponseDTO, HttpStatus.OK);
    }

    @GetMapping("random")
    public ResponseEntity<List<MovieResponseDTO>> random() {
        List<MovieResponseDTO> movies = movieService.random(3);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("genre/{genreId}")
    public ResponseEntity<List<MovieResponseDTO>> findByGenreId(@PathVariable long genreId) {
        List<MovieResponseDTO> movies = movieService.findByGenreId(genreId);

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
