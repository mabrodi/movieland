package org.dimchik.config;

import org.dimchik.client.NbuRateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;

@Configuration
public class ClientConfig {
    @Bean
    public NbuRateClient nbuRateClient(@Value("${client.rate.url}") String url) {
        RestClient restClient = RestClient.builder().baseUrl(url)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (req, res) -> handleError("NBU", req, res)
                )
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(NbuRateClient.class);
    }

    private void handleError(String clientName, HttpRequest request, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        String statusText = response.getStatusText();
        byte[] body = response.getBody().readAllBytes();

        String httpMethod = request.getMethod().name();
        String errorPath = request.getURI().getPath();

        String errorMessage = String.format(
                "[%s] API call [%s %s] failed with status: %s",
                clientName, httpMethod, errorPath, status
        );

        if (status.is4xxClientError()) {
            throw HttpClientErrorException.create(errorMessage, status, statusText, null, body, null);
        } else {
            throw HttpServerErrorException.create(errorMessage, status, statusText, null, body, null);
        }
    }
}
