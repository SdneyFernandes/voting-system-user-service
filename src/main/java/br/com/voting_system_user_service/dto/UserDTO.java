package br.com.voting_system_user_service.dto;

import br.com.voting_system_user_service.entity.*;
import br.com.voting_system_user_service.enums.*;


import lombok. *;



/**
 * @author fsdney
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	private Long id;
    private String userName;
    private String email;
    private Role role;  
    
    public UserDTO(User user) {
    	this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

}