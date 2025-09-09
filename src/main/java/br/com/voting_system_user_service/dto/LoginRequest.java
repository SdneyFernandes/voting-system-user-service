package br.com.voting_system_user_service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author fsdney
 * 
 */

public class LoginRequest {
    
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { 
        return email; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}