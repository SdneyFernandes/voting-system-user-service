package br.com.voting_system_user_service.entity;

import br.com.voting_system_user_service.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;


/**

 * @author fsdney

 */

@Entity
@Table(name = "table_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_user")
    @SequenceGenerator(name = "sequence_user", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "username", nullable = false)
    private String userName;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password é obrigatório")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;

    
    public User() {
    }

    public User(Long id, String userName, String email, String password, Role role, LocalDate createdAt) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    // MÉTODO DE CICLO DE VIDA (JPA)

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDate.now();
    }
}