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

    @PostConstruct // Este método será executado uma vez após a inicialização do componente
    public void bindMetrics() {
        // Métrica GAUGE para monitorar o número total de usuários no banco de dados.
        // Micrometer irá chamar a função userRepository::count periodicamente para obter o valor.
        Gauge.builder("database.users.total", userRepository, UserRepository::count)
            .description("Número total de usuários registrados na base de dados")
            .register(meterRegistry);
    }
}