package org.dimchik.web.controller;

import lombok.RequiredArgsConstructor;
import org.dimchik.service.GenreService;
import org.dimchik.dto.response.GenreResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<GenreResponse> findAll() {
        return genreService.findAll();
    }
}
