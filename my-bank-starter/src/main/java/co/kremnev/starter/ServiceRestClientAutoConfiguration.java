package co.kremnev.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@AutoConfiguration(beforeName = "org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration")
@ConditionalOnClass(name = {
        "org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager",
        "org.springframework.cloud.client.loadbalancer.LoadBalanced"
})
@ConditionalOnProperty(prefix = "my-bank.oauth2-client", name = "registration-id")
@EnableConfigurationProperties(MyBankRestClientProperties.class)
public class ServiceRestClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthorizedClientManager.class)
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build());
        return manager;
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager,
                                                MyBankRestClientProperties props) {
        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> props.getRegistrationId());
        interceptor.setPrincipalResolver(request ->
                new UsernamePasswordAuthenticationToken(props.getRegistrationId(), null));
        return RestClient.builder().requestInterceptor(interceptor);
    }
}
