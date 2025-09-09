package br.com.voting_system_user_service.controller;

import br.com.voting_system_user_service.dto.LoginRequest;
import br.com.voting_system_user_service.dto.UserDTO;
import br.com.voting_system_user_service.dto.RegisterRequest;
import br.com.voting_system_user_service.dto.AuthResponse;
import br.com.voting_system_user_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author fsdney
 */

//@Tag(name = "Autenticação", description = "Endpoints públicos para login e registro")
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        logger.info("Recebida requisição para registrar novo usuário");
        try {
            String message = authService.registerUser(request);
            return ResponseEntity.ok(new AuthResponse(message, null));
        } catch (RuntimeException ex) {
            logger.error("Erro ao registrar usuário: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(ex.getMessage(), null));
        } catch (Exception ex) {
            logger.error("Erro interno inesperado: {}", ex.getMessage());
            return ResponseEntity.status(500).body(new AuthResponse("Erro interno ao processar registro", null));
        }
    }

    //@Operation(summary = "Login", description = "Método para logar usuário")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        logger.info("Tentando login para email {}", request.getEmail());

        try {
            UserDTO user = authService.loginUser(request);
               return ResponseEntity.ok().body(Map.of(
                    "message", "Login successful",
                    "userId", user.getId(),
                    "role", user.getRole().name()
            ));
        } catch (RuntimeException ex) {
            logger.warn("Falha no login: {}", ex.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Credenciais inválidas"));
        }
    }

    //@Operation(summary = "Logout", description = "Método para logout do usuário")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        logger.info("Logout solicitado");
        authService.logoutUser();
        return ResponseEntity.ok("Logout successful");
    }
}