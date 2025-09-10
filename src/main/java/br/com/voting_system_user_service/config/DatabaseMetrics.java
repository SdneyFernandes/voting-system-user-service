package br.com.voting_system_user_service.config;

import br.com.voting_system_user_service.repository.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * @author fsdney
 */

@Configuration
public class DatabaseMetrics {

    private final MeterRegistry meterRegistry;
    private final UserRepository userRepository;

    public DatabaseMetrics(MeterRegistry meterRegistry, UserRepository userRepository) {
        this.meterRegistry = meterRegistry;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void bindMetrics() {
        Gauge.builder("database.users.total", userRepository, UserRepository::count)
            .description("Número total de usuários registrados na base de dados")
            .register(meterRegistry);
    }
}