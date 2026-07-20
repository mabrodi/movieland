package org.dimchik.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dimchik.security.PublicEndpoint;
import org.dimchik.service.GenreService;
import org.dimchik.dto.response.GenreResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Tag(name="${swagger.genres.tag.name}", description = "${swagger.genres.tag.description}")
public class GenreController {
    private final GenreService genreService;

    @PublicEndpoint
    @Operation(summary = "${swagger.genres.find-all.summary}", description = "${swagger.genres.find-all.description}")
    @GetMapping
    public List<GenreResponse> findAll() {
        return genreService.findAll();
    }
}
