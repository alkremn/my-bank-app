package co.kremnev.transfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final RestClient.Builder restClientBuilder;

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
}
