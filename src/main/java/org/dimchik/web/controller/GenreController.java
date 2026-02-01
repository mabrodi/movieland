package org.dimchik.web.controller;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.GenreDTO;
import org.dimchik.service.GenreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/genre")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<GenreDTO> findAll() {
        return genreService.findAll();
    }
}
