package br.com.voting_system_user_service.controller;

import br.com.voting_system_user_service.dto.LoginRequest;
import br.com.voting_system_user_service.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação", description = "Endpoints públicos para login e registro")
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "Método para logar usuário")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        logger.info("Tentando login para email {}", request.getEmail());

        // 🔹 autenticação fake ou via service
        boolean authenticated = authService.authenticate(request.getEmail(), request.getPassword());
        if (!authenticated) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // 🔹 cria cookies
        ResponseCookie emailCookie = ResponseCookie.from("email", request.getEmail())
                .httpOnly(true)
                .secure(true) // só HTTPS
                .sameSite("None")
                .path("/")
                .maxAge(3600)
                .build();

        ResponseCookie roleCookie = ResponseCookie.from("role", "USER")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(3600)
                .build();

        // 🔹 adiciona cookies na resposta
        response.addHeader(HttpHeaders.SET_COOKIE, emailCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());

        return ResponseEntity.ok("Login successful");
    }

    @Operation(summary = "Logout", description = "Método para logout do usuário")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        logger.info("Logout solicitado");

        // 🔹 apaga cookies
        ResponseCookie emailCookie = ResponseCookie.from("email", "")
                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0).build();
        ResponseCookie roleCookie = ResponseCookie.from("role", "")
                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, emailCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());

        return ResponseEntity.ok("Logout successful");
    }
}
