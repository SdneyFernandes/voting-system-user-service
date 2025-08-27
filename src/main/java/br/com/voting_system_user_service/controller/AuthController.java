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

@Tag(name = "Autenticação", description = "Endpoints públicos para login e registrrro")
@RestController
@RequestMapping("/api/users")
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
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    // 🔹 1. Autentica usuário
    User user = authService.authenticate(request.getUsername(), request.getPassword());

    // 🔹 2. Gera token
    String token = jwtService.generateToken(user);

    // 🔹 3. Cria cookies
    ResponseCookie userIdCookie = ResponseCookie.from("userId", String.valueOf(user.getId()))
            .httpOnly(true)          // 🔒 protegido contra JS
            .secure(true)            // 🔒 só HTTPS
            .sameSite("None")        // ✅ permite cross-site
            .path("/")
            .maxAge(3600)            // 1 hora
            .build();

    ResponseCookie roleCookie = ResponseCookie.from("role", user.getRole().name())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(3600)
            .build();

    ResponseCookie tokenCookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(3600)
            .build();

    // 🔹 4. Adiciona os cookies no header
    response.addHeader(HttpHeaders.SET_COOKIE, userIdCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());

    return ResponseEntity.ok("Login successful");
}


    @Operation(summary = "Logout", description = "Método para logout usuário")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        logger.info("Recebida requisição para logout de usuário (AuthController)");
        authService.logoutUser(response);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
} 