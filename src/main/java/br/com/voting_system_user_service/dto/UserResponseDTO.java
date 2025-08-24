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
public class UserResponseDTO {
	
    private String username;
    private String email;
} 