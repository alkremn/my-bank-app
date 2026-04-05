package co.kremnev.transfer.contract;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1234/jwks",
        "spring.security.oauth2.client.provider.transfer-service-client.token-uri=http://localhost:1234/token",
        "spring.security.oauth2.client.registration.transfer-service-client.client-id=test",
        "spring.security.oauth2.client.registration.transfer-service-client.client-secret=test",
        "spring.security.oauth2.client.registration.transfer-service-client.authorization-grant-type=client_credentials",
        "stubrunner.cloud.enabled=false"
})
@AutoConfigureStubRunner(
        ids = "co.kremnev:accounts-service:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class AccountsServiceContractTest {

    @StubRunnerPort("accounts-service")
    private int stubPort;

    @Test
    void shouldGetAccountByLogin() {
        RestClient restClient = RestClient.create("http://localhost:" + stubPort);

        Map<?, ?> response = restClient.get()
                .uri("/accounts/ivanov")
                .retrieve()
                .body(Map.class);

        assertThat(response).isNotNull();
        assertThat(response.get("login")).isEqualTo("ivanov");
        assertThat(response.get("name")).isEqualTo("Ivan Ivanov");
    }

    @Test
    void shouldTransferBetweenAccounts() {
        RestClient restClient = RestClient.create("http://localhost:" + stubPort);

        restClient.post()
                .uri("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"fromLogin\": \"ivanov\", \"toLogin\": \"petrov\", \"amount\": 300}")
                .retrieve()
                .toBodilessEntity();

        // If we get here without exception, the stub matched — contract is satisfied
    }
}
