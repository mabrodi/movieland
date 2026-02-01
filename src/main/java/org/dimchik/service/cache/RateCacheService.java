package org.dimchik.service.cache;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.client.RateClient;
import org.dimchik.dto.RateDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class RateCacheService {
    private volatile List<RateDTO> list = List.of();
    private final RateClient rateClient;

    public RateCacheService(RateClient rateClient) {
        this.rateClient = rateClient;
    }

    public List<RateDTO> findAll() {
        return new CopyOnWriteArrayList<>(list);
    }

    @PostConstruct
    private void init() {
        log.info("Initializing Rate cache on application startup");
        update();
    }

    @Scheduled(cron = "${cache.rate.refresh-cron}")
    private void update() {
        try {
            List<RateDTO> rates = rateClient.findAll();

            if (rates == null || rates.isEmpty()) {
                log.warn("rateClient returned empty list, keeping previous cache");
                return;
            }
            list = rates;
            log.info("Rate cache updated, total entries: {}", list.size());
        } catch (Exception e) {
            log.error("error updating rate cache: {} ", e.getMessage(), e);
        }
    }
}
