package org.dimchik.service.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.GenreDTO;
import org.dimchik.repository.GenreRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GenreCacheService {
    private final @Lazy GenreRepository genreRepository;
    private List<GenreDTO> list;

    public GenreCacheService(@Lazy GenreRepository genreRepository) {
        list = new ArrayList<>();
        this.genreRepository = genreRepository;
    }

    public List<GenreDTO> findAll() {
        return new ArrayList<>(list);
    }

    @PostConstruct
    private void init() {
        log.info("Initialization GenreCache for start application");
        update();
    }

    @Scheduled(fixedDelayString = "${cache.genre.refresh-interval}")
    private void update() {
        try {
            List<GenreDTO> genres = genreRepository.findAll().stream()
                    .map(g -> new GenreDTO(g.getId(), g.getName()))
                    .toList();
            list = genres;
            log.info("Genre cache updated, total entries: {}", genres.size());
        } catch (Exception e) {
            log.error("error updating genre cache: {} ", e.getMessage(), e);
        }
    }
}
