package org.dimchik.web.controller;

import jakarta.websocket.server.PathParam;
import org.dimchik.dto.ApiResponseDTO;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> findAll(
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

        return new ResponseEntity<>(ApiResponseDTO.success(movies), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> findById(@PathVariable long id) {
        MovieResponseDTO movieResponseDTO = movieService.findById(id);
        return new ResponseEntity<>(ApiResponseDTO.success(movieResponseDTO), HttpStatus.OK);
    }

    @GetMapping("random")
    public ResponseEntity<ApiResponseDTO> random() {
        List<MovieResponseDTO> movies = movieService.random(3);

        return new ResponseEntity<>(ApiResponseDTO.success(movies), HttpStatus.OK);
    }

    @GetMapping("genre/{genreId}")
    public ResponseEntity<ApiResponseDTO> findByGenreId(@PathVariable long genreId) {
        List<MovieResponseDTO> movies = movieService.findByGenreId(genreId);

        return new ResponseEntity<>(ApiResponseDTO.success(movies), HttpStatus.OK);
    }
}
