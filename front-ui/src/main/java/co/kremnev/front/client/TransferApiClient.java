package co.kremnev.front.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import co.kremnev.front.client.dto.TransferRequestDto;

import java.math.BigDecimal;

@Component
public class TransferApiClient {

    private final RestClient restClient;

    public TransferApiClient(RestClient transferRestClient) {
        this.restClient = transferRestClient;
    }

    @CircuitBreaker(name = "transfer", fallbackMethod = "transferFallback")
    public void transfer(String toLogin, BigDecimal amount) {
        restClient.post()
                .uri("/transfers")
                .body(new TransferRequestDto(toLogin, amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void transferFallback(String toLogin, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Transfer service is unavailable: " + t.getMessage());
    }
}
