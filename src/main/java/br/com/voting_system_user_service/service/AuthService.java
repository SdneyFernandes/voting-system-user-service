

package br.com.voting_system_user_service.service;

import br.com.voting_system_user_service.repository.UserRepository;
import br.com.voting_system_user_service.dto.*;
import br.com.voting_system_user_service.entity.User;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ... outros imports existentes
import org.springframework.http.HttpHeaders; // ✅ Adicionar
import org.springframework.http.ResponseCookie; // ✅ Adicionar
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MeterRegistry meterRegistry;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        meterRegistry.counter("usuario.registro.chamadas").increment();
        logger.info("Recebida requisição para registrar usuário com nome {}", request.getUserName());

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

    public void logoutUser(HttpServletResponse response) {
    logger.info("Recebida requisição para logout de usuário (AuthService)");

    // 🔹 Usar ResponseCookie consistentemente com o login
    ResponseCookie userIdCookie = ResponseCookie.from("userId", "")
        .httpOnly(false)
        .secure(true)
        .sameSite("None")
        .path("/")
        .domain("voting-system-api-gateway.onrender.com")
        .maxAge(0)
        .build();

    ResponseCookie roleCookie = ResponseCookie.from("role", "")
        .httpOnly(false)
        .secure(true)
        .sameSite("None")
        .path("/")
        .domain("voting-system-api-gateway.onrender.com")
        .maxAge(0)
        .build();

    // ✅ CORREÇÃO: Usar addHeader corretamente
    response.addHeader(HttpHeaders.SET_COOKIE, userIdCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());

    logger.info("Cookies de usuário removidos (AuthService)");
}
}
