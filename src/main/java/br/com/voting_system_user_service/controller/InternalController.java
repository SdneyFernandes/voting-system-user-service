package br.com.voting_system_user_service.controller;

import br.com.voting_system_user_service.service. *;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
public class InternalController {

    private final TokenService tokenService;

    public InternalController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateServiceToken(
        @RequestHeader("Authorization") String token) {
        
        boolean isValid = tokenService.isValidServiceToken(token);
        return ResponseEntity.ok(isValid);
    }
}