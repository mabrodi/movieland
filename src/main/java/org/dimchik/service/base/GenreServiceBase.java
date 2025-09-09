package org.dimchik.service.base;

import org.dimchik.dto.GenreResponseDTO;
import org.dimchik.repository.GenreResponse;
import org.dimchik.service.GenreService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreServiceBase implements GenreService {
    private final GenreResponse genreResponse;
    private final List<GenreResponseDTO> list;

    public GenreServiceBase(GenreResponse genreResponse) {
        this.genreResponse = genreResponse;
        this.list = new ArrayList<>();
    }

    @Override
    public List<GenreResponseDTO> findAll() {
        return list;
    }

    @Scheduled(fixedDelayString = "${spring.scheduled.genre}")
    public void update() {
        if (!list.isEmpty()) {
            list.clear();
        }

        genreResponse.findAll().stream().map(
                genre -> new GenreResponseDTO(genre.getId(), genre.getName())
        ).forEach(list::add);
    }
}
