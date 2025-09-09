package br.com.voting_system_user_service.dto;

/**
 * @author fsdney
 * 
 */

public class UserResponseDTO {
    
    private String username;
    private String email;

    public UserResponseDTO() {}

    public UserResponseDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}