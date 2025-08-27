package br.com.voting_system_user_service.dto;

import jakarta.validation.constraints. *;
import lombok.Data;

/**
 * @author fsdney
 */

@Data
public class LoginRequest {
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;

	public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    // SETTERS
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

}
 
