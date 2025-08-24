package br.com.voting_system_user_service.entity;


import br.com.voting_system_user_service.enums.Role;
import jakarta.persistence. *;
import jakarta.validation.constraints. *;
import lombok. *;


import java.time.LocalDate;

/**
 * @author fsdney
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
	private  String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role = Role.USER;
	
	@Column(name = "createdAt", nullable = false)
	private LocalDate createdAt;
	
	@PrePersist
    protected void prePersist() {
        this.createdAt = LocalDate.now();
    };
	
}
