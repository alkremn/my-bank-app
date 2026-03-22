package co.kremnev.front.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private RestClient buildRestClient(String baseUrl, OAuth2AuthorizedClientManager authorizedClientManager) {
        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> "keycloak");
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    public RestClient accountsRestClient(
            @Value("${services.accounts.url:http://localhost:8082}") String url,
            OAuth2AuthorizedClientManager authorizedClientManager) {
        return buildRestClient(url, authorizedClientManager);
    }

    @Bean
    public RestClient cashRestClient(
            @Value("${services.cash.url:http://localhost:8083}") String url,
            OAuth2AuthorizedClientManager authorizedClientManager) {
        return buildRestClient(url, authorizedClientManager);
    }

    @Bean
    public RestClient transferRestClient(
            @Value("${services.transfer.url:http://localhost:8084}") String url,
            OAuth2AuthorizedClientManager authorizedClientManager) {
        return buildRestClient(url, authorizedClientManager);
    }
}
