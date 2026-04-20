package co.kremnev.cash.service;

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
public class CashService {

    private final RestClient.Builder restClientBuilder;
    private final KafkaNotificationProducer notificationProducer;
    private final MeterRegistry meterRegistry;
    private final String accountsServiceUrl;

    public CashService(RestClient.Builder restClientBuilder,
                       KafkaNotificationProducer notificationProducer,
                       MeterRegistry meterRegistry,
                       @org.springframework.beans.factory.annotation.Value("${accounts-service.url:http://accounts-service}") String accountsServiceUrl) {
        this.restClientBuilder = restClientBuilder;
        this.notificationProducer = notificationProducer;
        this.meterRegistry = meterRegistry;
        this.accountsServiceUrl = accountsServiceUrl;
    }

    @CircuitBreaker(name = "cash-service", fallbackMethod = "depositFallback")
    public void deposit(String login, BigDecimal amount) {
        log.debug("Processing deposit: login={}, amount={}", login, amount);
        updateBalance(login, amount);
        notificationProducer.send(login, "Deposit: +" + amount);
        log.info("Deposit completed: login={}, amount={}", login, amount);
    }

    @CircuitBreaker(name = "cash-service", fallbackMethod = "withdrawFallback")
    public void withdraw(String login, BigDecimal amount) {
        log.debug("Processing withdrawal: login={}, amount={}", login, amount);
        try {
            updateBalance(login, amount.negate());
        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Withdrawal failed (insufficient funds): login={}, amount={}", login, amount);
            meterRegistry.counter("bank.withdrawal.failed", "login", login).increment();
            throw e;
        }
        notificationProducer.send(login, "Withdrawal: -" + amount);
        log.info("Withdrawal completed: login={}, amount={}", login, amount);
    }

    private void updateBalance(String login, BigDecimal amount) {
        restClientBuilder.build()
                .post()
                .uri(accountsServiceUrl + "/accounts/{login}/balance", login)
                .body(Map.of("amount", amount))
                .retrieve()
                .toBodilessEntity();
    }

    private void depositFallback(String login, BigDecimal amount, Throwable t) {
        log.error("Deposit fallback triggered: login={}, error={}", login, t.getMessage());
        throw new RuntimeException("Cash deposit service is unavailable: " + t.getMessage());
    }

    private void withdrawFallback(String login, BigDecimal amount, Throwable t) {
        log.error("Withdraw fallback triggered: login={}, error={}", login, t.getMessage());
        throw new RuntimeException("Cash withdraw service is unavailable: " + t.getMessage());
    }
}
