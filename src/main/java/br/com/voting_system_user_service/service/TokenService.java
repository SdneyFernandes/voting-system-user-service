package br.com.voting_system_user_service.service;

import org.springframework.stereotype.Service;


/**
 * @author fsdney
 */

@Service
public class TokenService {
    
    private static final String SERVICE_TOKEN_PREFIX = "Bearer ";
    private static final String VALID_SERVICE_TOKEN = "seu-token-secreto-de-servico";

    public boolean isValidServiceToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(SERVICE_TOKEN_PREFIX)) {
            return false;
        }
        
        String token = authHeader.substring(SERVICE_TOKEN_PREFIX.length());
        return VALID_SERVICE_TOKEN.equals(token);
    }
}
