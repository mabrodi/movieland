package org.dimchik.client;

import org.dimchik.dto.RateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class RateClient {
    private final RestClient restClient;

    private RateClient(RestClient.Builder builder, @Value("${client.rate.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public List<RateDTO> findAll() {
        List<Map<String, Object>> response = restClient.get()
                .uri("NBUStatService/v1/statdirectory/exchange?json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null) {
            return List.of();
        }

        return response.stream()
                .map(item -> new RateDTO(
                        ((Number) item.get("r030")).longValue(),
                        (String) item.get("txt"),
                        (String) item.get("cc"),
                        ((Number) item.get("rate")).doubleValue()
                ))
                .toList();
    }

}
