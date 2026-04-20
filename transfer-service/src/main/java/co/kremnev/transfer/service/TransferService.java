package co.kremnev.transfer.service;

import co.kremnev.starter.KafkaNotificationProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final RestClient.Builder restClientBuilder;
    private final KafkaNotificationProducer notificationProducer;
    private final MeterRegistry meterRegistry;

    @CircuitBreaker(name = "transfer-service", fallbackMethod = "transferFallback")
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        log.debug("Processing transfer: from={}, to={}, amount={}", fromLogin, toLogin, amount);
        try {
            restClientBuilder.build()
                    .post()
                    .uri("http://accounts-service/accounts/transfer")
                    .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "amount", amount))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Transfer failed (insufficient funds): from={}, to={}, amount={}", fromLogin, toLogin, amount);
            meterRegistry.counter("bank.transfer.failed", "sender", fromLogin, "receiver", toLogin).increment();
            throw e;
        }

        notificationProducer.send(fromLogin, "Transfer sent: -" + amount + " to " + toLogin);
        notificationProducer.send(toLogin, "Transfer received: +" + amount + " from " + fromLogin);
        log.info("Transfer completed: from={}, to={}, amount={}", fromLogin, toLogin, amount);
    }

    private void transferFallback(String fromLogin, String toLogin, BigDecimal amount, Throwable t) {
        log.error("Transfer fallback triggered: from={}, to={}, error={}", fromLogin, toLogin, t.getMessage());
        throw new RuntimeException("Transfer service is unavailable: " + t.getMessage());
    }
}
