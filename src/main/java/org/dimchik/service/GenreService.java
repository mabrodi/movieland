package org.dimchik.service;

import org.dimchik.dto.GenreDTO;

import java.util.List;

public interface GenreService {
    List<GenreDTO> findAll();
}
