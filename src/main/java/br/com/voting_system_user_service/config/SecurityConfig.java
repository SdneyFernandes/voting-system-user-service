// src/main/java/br/com/voting_system_user_service/config/SecurityConfig.java

package br.com.voting_system_user_service.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/api/users/register",
                                "/api/users/login",
                                "/api/auth/service-token",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/h2-console/**",
                                "/api/users/{id}"
                        ).permitAll()
                        .requestMatchers("/api/users").hasRole("ADMIN")
                        .requestMatchers("/api/internal/**").permitAll()
                        .requestMatchers("/api/users/{userName}").hasRole("ADMIN")
                        .requestMatchers("/api/votes_session/create").hasRole("ADMIN")
                        .requestMatchers("/api/votes_session/{id}").hasRole("ADMIN")
                        .requestMatchers("/api/votes_session/{id}/delete").hasRole("ADMIN")
                        .requestMatchers("/api/votes_session").authenticated()
                        .requestMatchers("/api/votes_session/{id}").authenticated()
                        .requestMatchers("/api/votes_session/{id}/results").authenticated()
                        .requestMatchers("/api/votes/{voteSessionId}/cast").authenticated()
                        .requestMatchers("/api/users/me").authenticated()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(preAuthenticatedProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AbstractPreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() {
        AbstractPreAuthenticatedProcessingFilter filter = new AbstractPreAuthenticatedProcessingFilter() {
            @Override
            protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
                return request.getHeader("X-User-Id");
            }

            @Override
            protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
                return request.getHeader("X-User-Role");
            }
        };
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            String userId = (String) authentication.getPrincipal();
            String role = (String) authentication.getCredentials();

            logger.info("Cabeçalhos recebidos - X-User-Id: {}, X-User-Role: {}", userId, role);

            if (userId == null || role == null) {
                throw new BadCredentialsException("Cabeçalhos X-User-Id e X-User-Role são obrigatórios");
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

            logger.info("Usuário autenticado corretamente: {}", userId);
            return auth;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 
