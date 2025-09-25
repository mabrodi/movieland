package org.dimchik.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.GenreDTO;
import org.dimchik.repository.GenreRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GenreCache {
    private final GenreRepository genreRepository;
    private List<GenreDTO> list;

    public GenreCache(GenreRepository genreRepository) {
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

    @Scheduled(fixedDelayString = "${cache.genre-update-delay}")
    private void update() {
        try {
            List<GenreDTO> newList = genreRepository.findAll().stream()
                    .map(g -> new GenreDTO(g.getId(), g.getName()))
                    .toList();
            list = newList;
            log.info("Genre cache updated, total entries: {}", newList.size());
        } catch (Exception e) {
            log.error("error updating genre cache: {} ", e.getMessage(), e);
        }
    }
}
