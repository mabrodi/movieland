package org.dimchik.service.base;

import org.dimchik.dto.GenreResponseDTO;
import org.dimchik.repository.GenreResponse;
import org.dimchik.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceBase implements GenreService {
    private final GenreResponse genreResponse;

    public GenreServiceBase(GenreResponse genreResponse) {
        this.genreResponse = genreResponse;
    }

    @Override
    public List<GenreResponseDTO> findAll() {
        List<GenreResponseDTO> genreResponseDTOList = null;
        try {
            genreResponseDTOList = genreResponse.findAll().stream().map(
                    genre -> new GenreResponseDTO(genre.getId(), genre.getName())
            ).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return genreResponseDTOList;
    }
}
