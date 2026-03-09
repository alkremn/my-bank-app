package co.kremnev.front.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import co.kremnev.front.client.dto.CashRequestDto;

import java.math.BigDecimal;

@Component
public class CashApiClient {

    private final RestClient restClient;

    public CashApiClient(RestClient gatewayRestClient) {
        this.restClient = gatewayRestClient;
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "depositFallback")
    public void deposit(BigDecimal amount) {
        restClient.post()
                .uri("/api/cash/deposit")
                .body(new CashRequestDto(amount))
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "withdrawFallback")
    public void withdraw(BigDecimal amount) {
        restClient.post()
                .uri("/api/cash/withdraw")
                .body(new CashRequestDto(amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void depositFallback(BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash service is unavailable: " + t.getMessage());
    }

    private void withdrawFallback(BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash service is unavailable: " + t.getMessage());
    }
}
