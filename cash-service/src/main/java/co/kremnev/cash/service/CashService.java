package co.kremnev.cash.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CashService {

    private final RestClient.Builder restClientBuilder;

    @CircuitBreaker(name = "cash-service", fallbackMethod = "depositFallback")
    public void deposit(String login, BigDecimal amount) {
        updateBalance(login, amount);
        notify(login, "Deposit: +" + amount);
    }

    @CircuitBreaker(name = "cash-service", fallbackMethod = "withdrawFallback")
    public void withdraw(String login, BigDecimal amount) {
        updateBalance(login, amount.negate());
        notify(login, "Withdrawal: -" + amount);
    }

    private void updateBalance(String login, BigDecimal amount) {
        restClientBuilder.build()
                .post()
                .uri("http://accounts-service/accounts/{login}/balance", login)
                .body(Map.of("amount", amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void notify(String login, String message) {
        restClientBuilder.build()
                .post()
                .uri("http://notification-service/notifications")
                .body(Map.of("login", login, "message", message))
                .retrieve()
                .toBodilessEntity();
    }

    private void depositFallback(String login, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash deposit service is unavailable: " + t.getMessage());
    }

    private void withdrawFallback(String login, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Cash withdraw service is unavailable: " + t.getMessage());
    }
}
