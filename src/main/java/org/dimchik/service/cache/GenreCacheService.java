package org.dimchik.service.cache;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.GenreDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreCacheService {
    @PersistenceContext
    private final EntityManager entityManager;

    private volatile List<GenreDTO> list;

    public List<GenreDTO> findAll() {
        return Collections.unmodifiableList(list);
    }

    @PostConstruct
    @Scheduled(cron = "${cache.genre.refresh-cron}")
    private void refreshCache() {
        try {
            List<GenreDTO> genres = entityManager.createQuery(
                    "select new org.dimchik.dto.GenreDTO(g.id, g.name) from Genre g",
                    GenreDTO.class
            ).getResultList();

            list = genres;

            log.info("Genre cache updated, total entries: {}", genres.size());
        } catch (Exception e) {
            log.error("Error updating genre cache: {}", e.getMessage(), e);
        }
    }
}
