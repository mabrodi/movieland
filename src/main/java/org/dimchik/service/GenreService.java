package org.dimchik.service;

import org.dimchik.dto.GenreResponseDTO;

import java.util.List;

public interface GenreService {
    List<GenreResponseDTO> findAll();
}
