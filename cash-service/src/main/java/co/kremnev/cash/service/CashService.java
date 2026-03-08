package co.kremnev.cash.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CashService {

    private final RestClient.Builder restClientBuilder;

    public void deposit(String login, BigDecimal amount) {
        updateBalance(login, amount);
        notify(login, "Deposit: +" + amount);
    }

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
}
