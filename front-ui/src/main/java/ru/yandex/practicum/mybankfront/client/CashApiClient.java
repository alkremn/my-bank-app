package ru.yandex.practicum.mybankfront.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.client.dto.CashRequestDto;

import java.math.BigDecimal;

@Component
public class CashApiClient {

    private final RestClient restClient;

    public CashApiClient(RestClient gatewayRestClient) {
        this.restClient = gatewayRestClient;
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "depositFallback")
    public void deposit(String login, BigDecimal amount) {
        restClient.post()
                .uri("/api/cash/deposit")
                .body(new CashRequestDto(login, amount))
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "withdrawFallback")
    public void withdraw(String login, BigDecimal amount) {
        restClient.post()
                .uri("/api/cash/withdraw")
                .body(new CashRequestDto(login, amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void depositFallback(String login, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash service is unavailable: " + t.getMessage());
    }

    private void withdrawFallback(String login, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash service is unavailable: " + t.getMessage());
    }
}
