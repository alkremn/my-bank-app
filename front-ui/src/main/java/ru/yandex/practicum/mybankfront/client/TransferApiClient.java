package ru.yandex.practicum.mybankfront.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.client.dto.TransferRequestDto;

import java.math.BigDecimal;

@Component
public class TransferApiClient {

    private final RestClient restClient;

    public TransferApiClient(RestClient gatewayRestClient) {
        this.restClient = gatewayRestClient;
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "transferFallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        restClient.post()
                .uri("/api/transfers")
                .body(new TransferRequestDto(fromLogin, toLogin, amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void transferFallback(String fromLogin, String toLogin, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Transfer service is unavailable: " + t.getMessage());
    }
}
