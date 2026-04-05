package co.kremnev.front.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import co.kremnev.front.client.dto.CashRequestDto;

import java.math.BigDecimal;

@Component
public class CashApiClient {

    private final RestClient restClient;

    public CashApiClient(RestClient cashRestClient) {
        this.restClient = cashRestClient;
    }

    @CircuitBreaker(name = "cash", fallbackMethod = "depositFallback")
    public void deposit(BigDecimal amount) {
        restClient.post()
                .uri("/cash/deposit")
                .body(new CashRequestDto(amount))
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "cash", fallbackMethod = "withdrawFallback")
    public void withdraw(BigDecimal amount) {
        restClient.post()
                .uri("/cash/withdraw")
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
