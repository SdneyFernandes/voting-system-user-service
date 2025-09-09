package br.com.voting_system_user_service.dto;

import br.com.voting_system_user_service.entity.User;
import br.com.voting_system_user_service.enums.Role;


/**
 * @author fsdney
 * 
 */

public class UserDTO {
    
    private Long id;
    private String userName;
    private String email;
    private Role role;
    
    public UserDTO() {}

    public UserDTO(Long id, String userName, String email, Role role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }
    
    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}