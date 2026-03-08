package ru.yandex.practicum.mybankfront.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.client.dto.AccountResponse;
import ru.yandex.practicum.mybankfront.client.dto.AccountUpdateRequest;

import java.util.List;

@Component
public class AccountApiClient {

    private final RestClient restClient;

    public AccountApiClient(RestClient gatewayRestClient) {
        this.restClient = gatewayRestClient;
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "getByLoginFallback")
    public AccountResponse getByLogin(String login) {
        return restClient.get()
                .uri("/api/accounts/{login}", login)
                .retrieve()
                .body(AccountResponse.class);
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "getAllFallback")
    public List<AccountResponse> getAll() {
        return restClient.get()
                .uri("/api/accounts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "gateway", fallbackMethod = "updateFallback")
    public AccountResponse update(String login, String name, java.time.LocalDate birthdate) {
        return restClient.put()
                .uri("/api/accounts/{login}", login)
                .body(new AccountUpdateRequest(login, name, birthdate))
                .retrieve()
                .body(AccountResponse.class);
    }

    private AccountResponse getByLoginFallback(String login, Throwable t) {
        throw new RuntimeException("Account service is unavailable: " + t.getMessage());
    }

    private List<AccountResponse> getAllFallback(Throwable t) {
        throw new RuntimeException("Account service is unavailable: " + t.getMessage());
    }

    private AccountResponse updateFallback(String login, String name, java.time.LocalDate birthdate, Throwable t) {
        throw new RuntimeException("Account service is unavailable: " + t.getMessage());
    }
}
