package org.dimchik.repository.cache;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.GenreDTO;
import org.dimchik.entity.Genre;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GenreCacheRepository {
    private final EntityManager em;
    private List<GenreDTO> list;

    private GenreCacheRepository(EntityManager em) {
        list = new ArrayList<>();
        this.em = em;
    }

    public List<GenreDTO> findAll() {
        return list;
    }

    @PostConstruct
    private void init() {
        log.info("Initialization GenreCache for start application");
        update();
    }

    @Scheduled(fixedDelayString = "${cache.genre-update-delay}")
    private void update() {
        try {
            List<GenreDTO> newList = em.createQuery("from Genre", Genre.class)
                    .getResultList()
                    .stream()
                    .map(g -> new GenreDTO(g.getId(), g.getName()))
                    .toList();
            list = newList;
            log.info("Genre cache updated, total entries: {}", newList.size());
        } catch (Exception e) {
            log.error("error updating genre cache: {} ", e.getMessage(), e);
        }
    }
}
