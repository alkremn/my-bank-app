package ru.yandex.practicum.mybankfront.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${keycloak.logout-uri:http://localhost:8180/realms/my-bank/protocol/openid-connect/logout}")
    private String keycloakLogoutUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> {})
                .logout(logout -> logout
                        .logoutSuccessHandler(keycloakLogoutSuccessHandler())
                );
        return http.build();
    }

    private LogoutSuccessHandler keycloakLogoutSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String redirectUri = URLEncoder.encode(
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort(),
                    StandardCharsets.UTF_8);
            response.sendRedirect(keycloakLogoutUri
                    + "?client_id=front-ui-client"
                    + "&post_logout_redirect_uri=" + redirectUri);
        };
    }
}
