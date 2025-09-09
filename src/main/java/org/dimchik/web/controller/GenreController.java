package org.dimchik.web.controller;

import org.dimchik.dto.GenreResponseDTO;
import org.dimchik.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDTO>> findAll() {
        List<GenreResponseDTO> genreResponseDTOList = genreService.findAll();

        return new ResponseEntity<>(genreResponseDTOList, HttpStatus.OK);
    }
}
