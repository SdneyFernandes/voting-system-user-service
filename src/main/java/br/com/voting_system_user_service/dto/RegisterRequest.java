package br.com.voting_system_user_service.dto;

import br.com.voting_system_user_service.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;



/**
 * @author fsdney
 */

@Data
public class RegisterRequest {
	
	@NotBlank(message = "Nome é obrigatório")
	private String userName;
	
	@NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
	private String email;
	
	@NotBlank(message = "Senha é obrigatória")
	private String password;
	
	@NotNull(message = "Role é obrigatorio")
	   
	private Role role;

}