package co.kremnev.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final RestClient.Builder restClientBuilder;

    @CircuitBreaker(name = "transfer-service", fallbackMethod = "transferFallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        restClientBuilder.build()
                .post()
                .uri("http://accounts-service/accounts/transfer")
                .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "amount", amount))
                .retrieve()
                .toBodilessEntity();

        notify(fromLogin, "Transfer sent: -" + amount + " to " + toLogin);
        notify(toLogin, "Transfer received: +" + amount + " from " + fromLogin);
    }

    private void notify(String login, String message) {
        restClientBuilder.build()
                .post()
                .uri("http://notification-service/notifications")
                .body(Map.of("login", login, "message", message))
                .retrieve()
                .toBodilessEntity();
    }

    private void transferFallback(String fromLogin, String toLogin, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Transfer service is unavailable: " + t.getMessage());
    }
}
