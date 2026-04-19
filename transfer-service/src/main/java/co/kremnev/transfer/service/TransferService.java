package co.kremnev.transfer.service;

import co.kremnev.starter.KafkaNotificationProducer;
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
    private final KafkaNotificationProducer notificationProducer;

    @CircuitBreaker(name = "transfer-service", fallbackMethod = "transferFallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        restClientBuilder.build()
                .post()
                .uri("http://accounts-service/accounts/transfer")
                .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "amount", amount))
                .retrieve()
                .toBodilessEntity();

        notificationProducer.send(fromLogin, "Transfer sent: -" + amount + " to " + toLogin);
        notificationProducer.send(toLogin, "Transfer received: +" + amount + " from " + fromLogin);
    }

    private void transferFallback(String fromLogin, String toLogin, BigDecimal amount, Throwable t) {
        throw new RuntimeException("Transfer service is unavailable: " + t.getMessage());
    }
}
