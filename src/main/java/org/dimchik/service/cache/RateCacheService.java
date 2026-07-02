package org.dimchik.service.cache;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.client.NbuRateClient;
import org.dimchik.dto.RateDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateCacheService {
    private volatile List<RateDTO> list;
    private final NbuRateClient rateClient;

    public List<RateDTO> findAll() {
        return Collections.unmodifiableList(list);
    }

    @PostConstruct
    @Scheduled(cron = "${cache.rate.refresh-cron}")
    private void update() {
        try {
            List<RateDTO> rates = rateClient.findAll();

            if (rates.isEmpty()) {
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
