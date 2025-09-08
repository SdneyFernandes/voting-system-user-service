package br.com.voting_system_user_service.service;

import br.com.voting_system_user_service.repository.UserRepository;
import br.com.voting_system_user_service.dto.*;
import br.com.voting_system_user_service.entity.User;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value; 
import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MeterRegistry meterRegistry;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

@Value("${REGISTRATION_WHITELIST_EMAILS:}") // <-- ADICIONE ESTA LINHA
    private List<String> allowedEmails;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(RegisterRequest request) {
       long startTime = System.currentTimeMillis();
    meterRegistry.counter("usuario.registro.chamadas").increment();
    logger.info("Reccebida requisição para registrar usuário com nome {}", request.getUserName());

    // --- LÓGICA DE VERIFICAÇÃO ADICIONADA ---
    if (allowedEmails != null && !allowedEmails.isEmpty()) {
        if (!allowedEmails.contains(request.getEmail())) {
            logger.warn("[REGISTRO BLOQUEADO] Tentativa de registro com email não autorizado: {}", request.getEmail());
            throw new RuntimeException("Este email não está autorizado a se registrar no momento.");
        }
    }
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Tentativa de registro falhou, ja existe um usuario cadastrado com esse e-mail {}", request.getEmail());
            throw new RuntimeException("E-mail já cadastrado.");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        long duration = System.currentTimeMillis() - startTime;
        meterRegistry.timer("usuario.registro.tempo").record(duration, TimeUnit.MILLISECONDS);
        logger.info("Usuário com nome {} registrado com sucesso", request.getUserName());

        return "Usuário registrado com sucesso!";
    }

    public UserDTO loginUser(LoginRequest request) {
        // ... (este método continua igual)
        long startTime = System.currentTimeMillis();
        meterRegistry.counter("usuario.login.chamadas").increment();
        logger.info("Recebida requisição de login com e-mail: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login falhou: e-mail não encontrado - {}", request.getEmail());
                    return new RuntimeException("Credenciais inválidas");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login falhou: senha inválida para o e-mail {}", request.getEmail());
            throw new RuntimeException("Credenciais inválidas");
        }

        long duration = System.currentTimeMillis() - startTime;
        meterRegistry.timer("usuario.login.tempo").record(duration, TimeUnit.MILLISECONDS);
        logger.info("Usuário {} autenticado com sucesso", user.getUserName());

        return new UserDTO(user);
    }

    // <-- MUDANÇA: Método simplificado para não manipular cookies
    public void logoutUser() {
        logger.info("Logout processado no AuthService (sem manipulação de cookies)");
        // A lógica de criar e adicionar cookies com maxAge(0) foi removida.
        // O Gateway será responsável por limpar os cookies do navegador.
        // Aqui você poderia adicionar outra lógica se necessário (ex: invalidar um token no BD).
    }
}