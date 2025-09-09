package br.com.voting_system_user_service.service;

import br.com.voting_system_user_service.repository.UserRepository;
import br.com.voting_system_user_service.dto.*;
import br.com.voting_system_user_service.entity.User;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.micrometer.core.annotation.Timed;


/**
 * @author fsdney
 */


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MeterRegistry meterRegistry;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${REGISTRATION_WHITELIST_EMAILS:}")
    private List<String> allowedEmails;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
        this.passwordEncoder = passwordEncoder;
    }

    @Timed("usuario.registro.tempo")
    public String registerUser(RegisterRequest request) {
        logger.info("Recebida requisição para registrar usuário com nome {}", request.getUserName());
        
        if (allowedEmails != null && !allowedEmails.isEmpty()) {
            if (!allowedEmails.contains(request.getEmail())) {
                logger.warn("[REGISTRO BLOQUEADO] Tentativa de registro com email não autorizado: {}", request.getEmail());
                meterRegistry.counter("usuario.registro.total", "status", "falha", "reason", "not_in_whitelist").increment();
                throw new RuntimeException("Este email não está autorizado a se registrar no momento.");
            }
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Tentativa de registro falhou, ja existe um usuario cadastrado com esse e-mail {}", request.getEmail());
            meterRegistry.counter("usuario.registro.total", "status", "falha", "reason", "email_exists").increment();
            throw new RuntimeException("E-mail já cadastrado.");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);

        meterRegistry.counter("usuario.registro.total", "status", "sucesso").increment();
        logger.info("Usuário com nome {} registrado com sucesso", request.getUserName());
        return "Usuário registrado com sucesso!";
    }

    @Timed("usuario.login.tempo") 
    public UserDTO loginUser(LoginRequest request) {
        logger.info("Recebida requisição de login com e-mail: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login falhou: e-mail não encontrado - {}", request.getEmail());
                    meterRegistry.counter("usuario.login.total", "status", "falha", "reason", "not_found").increment();
                    return new RuntimeException("Credenciais inválidas");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login falhou: senha inválida para o e-mail {}", request.getEmail());
            meterRegistry.counter("usuario.login.total", "status", "falha", "reason", "bad_credentials").increment();
            throw new RuntimeException("Credenciais inválidas");
        }

        logger.info("Usuário {} autenticado com sucesso", user.getUserName());
        meterRegistry.counter("usuario.login.total", "status", "sucesso", "role", user.getRole().name()).increment();
        
        return new UserDTO(user);
    }

    public void logoutUser() {
        meterRegistry.counter("usuario.logout.chamadas").increment();
        logger.info("Logout processado no AuthService (sem manipulação de cookies)");
    }
}