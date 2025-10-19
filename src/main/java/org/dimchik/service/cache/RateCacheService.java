package org.dimchik.service.cache;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.client.RateClient;
import org.dimchik.dto.RateDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RateCacheService {
    private List<RateDTO> list;
    private final RateClient rateClient;

    public RateCacheService(RateClient rateClient) {
        this.rateClient = rateClient;
        this.list = new ArrayList<>();
    }

    public List<RateDTO> findAll() {
        return new ArrayList<>(list);
    }

    @PostConstruct
    private void init() {
        update();
    }

    @Scheduled(cron = "${cache.rate.refresh-cron}")
    private void update() {
        try {
            list = rateClient.findAll();
            log.info("Rate cache updated, total entries: {}", list.size());
        } catch (Exception e) {
            log.error("error updating rate cache: {} ", e.getMessage(), e);
        }
    }
}
