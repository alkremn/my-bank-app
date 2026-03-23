package co.kremnev.front.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ServiceUrlProperties.class)
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
    public RestClient accountsRestClient(ServiceUrlProperties props, OAuth2AuthorizedClientManager mgr) {
        return buildRestClient(props.accounts().url(), mgr);
    }

    @Bean
    public RestClient cashRestClient(ServiceUrlProperties props, OAuth2AuthorizedClientManager mgr) {
        return buildRestClient(props.cash().url(), mgr);
    }

    @Bean
    public RestClient transferRestClient(ServiceUrlProperties props, OAuth2AuthorizedClientManager mgr) {
        return buildRestClient(props.transfer().url(), mgr);
    }
}
