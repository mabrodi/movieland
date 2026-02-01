package org.dimchik.service.cache;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.GenreDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreCacheService {
    @PersistenceContext
    private EntityManager em;

    private volatile List<GenreDTO> list = List.of();

    public List<GenreDTO> findAll() {
        return new CopyOnWriteArrayList<>(list);
    }

    @PostConstruct
    public void init() {
        log.info("Initializing Genre cache on application startup");
        refreshCache();
    }

    @Scheduled(fixedDelayString = "${cache.genre.refresh-interval}")
    public void refreshCache() {
        try {
            List<GenreDTO> genres = em.createQuery(
                    "select new org.dimchik.dto.GenreDTO(g.id, g.name) from Genre g",
                    GenreDTO.class
            ).getResultList();

            if (genres == null || genres.isEmpty()) {
                log.warn("genre returned empty list, keeping previous cache");
                return;
            }

            list = genres;

            log.info("Genre cache updated, total entries: {}", genres.size());
        } catch (Exception e) {
            log.error("Error updating genre cache: {}", e.getMessage(), e);
        }
    }
}
