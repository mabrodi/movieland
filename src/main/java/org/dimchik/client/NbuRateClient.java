package org.dimchik.client;

import org.dimchik.dto.RateDTO;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface NbuRateClient {
    @GetExchange("NBUStatService/v1/statdirectory/exchange?json")
    List<RateDTO> findAll();
}


