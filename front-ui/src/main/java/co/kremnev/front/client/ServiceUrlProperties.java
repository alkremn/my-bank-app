package co.kremnev.front.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record ServiceUrlProperties(
        ServiceUrl accounts,
        ServiceUrl cash,
        ServiceUrl transfer
) {
    public record ServiceUrl(String url) {}
}
