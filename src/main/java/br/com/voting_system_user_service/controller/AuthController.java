package br.com.voting_system_user_service.controller;

import br.com.voting_system_user_service.service.AuthService;
import br.com.voting_system_user_service.dto.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação", description = "Endpoints públicos para login e registro")
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController (AuthService authService) {
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

    @Operation(summary = "Login", description = "Método para logar usuário")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        logger.info("Recebida requisição para login de usuário");
        try {
            UserDTO user = authService.loginUser(request);

            Cookie userIdCookie = new Cookie("userId", user.getId().toString());
            userIdCookie.setHttpOnly(false);
            userIdCookie.setSecure(false);
            userIdCookie.setPath("/");
            userIdCookie.setMaxAge(3600);
            response.addCookie(userIdCookie);

            Cookie roleCookie = new Cookie("role", user.getRole().toString());
            roleCookie.setHttpOnly(false);
            roleCookie.setSecure(false);
            roleCookie.setPath("/");
            roleCookie.setMaxAge(3600);
            response.addCookie(roleCookie);

            return ResponseEntity.ok(new AuthResponse("Login realizado com sucesso", null));
        } catch (RuntimeException ex) {
            logger.error("Erro ao realizar login: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(ex.getMessage(), null));
        }
    }

    @Operation(summary = "Logout", description = "Método para logout usuário")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        logger.info("Recebida requisição para logout de usuário (AuthController)");
        authService.logoutUser(response);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
} 