package br.com.voting_system_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import br.com.voting_system_user_service.entity.User;


/**
 * @author fsdney
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String username);
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
}
