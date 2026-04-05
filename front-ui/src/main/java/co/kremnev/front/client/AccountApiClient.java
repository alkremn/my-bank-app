package co.kremnev.front.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import co.kremnev.front.client.dto.AccountResponse;
import co.kremnev.front.client.dto.AccountUpdateRequest;

import java.util.List;

@Component
public class AccountApiClient {

    private static final ParameterizedTypeReference<List<AccountResponse>> ACCOUNT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public AccountApiClient(RestClient accountsRestClient) {
        this.restClient = accountsRestClient;
    }

    @CircuitBreaker(name = "accounts", fallbackMethod = "getByLoginFallback")
    public AccountResponse getByLogin(String login) {
        return restClient.get()
                .uri("/accounts/{login}", login)
                .retrieve()
                .body(AccountResponse.class);
    }

    @CircuitBreaker(name = "accounts", fallbackMethod = "getAllFallback")
    public List<AccountResponse> getAll() {
        return restClient.get()
                .uri("/accounts")
                .retrieve()
                .body(ACCOUNT_LIST_TYPE);
    }

    @CircuitBreaker(name = "accounts", fallbackMethod = "updateFallback")
    public AccountResponse update(String login, String name, java.time.LocalDate birthdate) {
        return restClient.put()
                .uri("/accounts/{login}", login)
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
