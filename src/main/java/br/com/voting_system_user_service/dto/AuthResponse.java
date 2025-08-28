package br.com.voting_system_user_service.dto;



/**
 * @author fsdney
 * 
 * DTO para resposta de autenticação.
 * Contém uma mensagem e opcionalmente um token JWT.
 */

public record AuthResponse(String message, UserDTO user) {
    
}

