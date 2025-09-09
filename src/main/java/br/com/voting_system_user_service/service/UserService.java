package br.com.voting_system_user_service.service;

import br.com.voting_system_user_service.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import br.com.voting_system_user_service.dto.UserDTO;
import br.com.voting_system_user_service.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import io.micrometer.core.annotation.Timed;

/**
 * @author fsdney
 */

@Service
public class UserService {
	
private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
    private final MeterRegistry meterRegistry;
	private final UserRepository userRepository;
	
	 public UserService(UserRepository userRepository, MeterRegistry meterRegistry) {
	        this.userRepository = userRepository;
	        this.meterRegistry = meterRegistry;  
 }
	
	@PreAuthorize("hasRole('ADMIN')")
    @Timed("usuarios.operacoes.tempo", "operacao", "listar_todos")
    public List<UserDTO> getAllUsers() {
        logger.info("Recebida requisição para listar todos os usuários.");
        meterRegistry.counter("usuarios.operacoes.chamadas", "operacao", "listar_todos").increment();
        
        List<User> users = userRepository.findAll();
        
        if (users.isEmpty()) {
            logger.warn("Lista de usuários retornou vazia.");
        } else {
            logger.info("{} usuários encontrados", users.size());
        } 
        
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }
	
	
	
	@Timed("usuarios.operacoes.tempo", "operacao", "buscar_por_id")
    public Optional<UserDTO> getUserById(Long id) {
        logger.info("Recebida requisição para buscar usuário com ID {}", id);
        meterRegistry.counter("usuarios.operacoes.chamadas", "operacao", "buscar_por_id").increment();
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            logger.info("Usuário encontrado {}",  user.get().getUserName());
            meterRegistry.counter("usuarios.busca.resultado", "tipo", "id", "status", "encontrado").increment();
        } else {
            logger.warn("Usuário com ID {} não encontrado.", id);
            meterRegistry.counter("usuarios.busca.resultado", "tipo", "id", "status", "nao_encontrado").increment();
        }
        
        return user.map(UserDTO::new);
    }
	
	
	 @PreAuthorize("hasRole('ADMIN')")
	 @Timed("usuarios.operacoes.tempo", "operacao", "buscar_por_Nome")
	 public Optional<UserDTO> getUserByName(String userName) {
	        logger.info("Recebida requisição para buscar usuário com Nome {}", userName);
	        meterRegistry.counter("usuarios.operacoes.chamadas", "operacao", "buscar_por_Nome").increment();
	        Optional<User> user = userRepository.findByUserName(userName);

	        if (user.isPresent()) {
	            logger.info("Usuário encontrado {}", user.get());
	            meterRegistry.counter("usuarios.busca.resultado", "tipo", "Nome", "status", "encontrado").increment();
	        } else {
	            logger.warn("Usuário com nome {} não encontrado.", userName);
	            meterRegistry.counter("usuarios.busca.resultado", "tipo", "Nome", "status", "nao_encontrado").increment();
	        }

	        return user.map(UserDTO::new);
	    }
	
	@PreAuthorize("hasRole('ADMIN')")
    @Timed("usuarios.operacoes.tempo", "operacao", "deletar_por_id")
    public boolean deleteUserById(Long id) {
        logger.info("Recebida requisição para deletar usuário com ID {}", id);
        meterRegistry.counter("usuarios.operacoes.chamadas", "operacao", "deletar_por_id").increment();

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Usuário deletado com sucesso.");
            meterRegistry.counter("usuarios.delete.resultado", "status", "sucesso").increment();
            return true;
        } else {
            logger.warn("Usuário com ID {} não encontrado para exclusão.", id);
            meterRegistry.counter("usuarios.delete.resultado", "status", "nao_encontrado").increment();
            return false;
        }
    }
	 
	 
	 @PreAuthorize("hasRole('ADMIN')")
	 @Timed("usuarios.operacoes.tempo", "operacao", "deletar_por_nome")
	 public boolean deleteUserByName(String userName) {
	        logger.info("Recebida requisição para deletar usuário com nome {}", userName);
	        meterRegistry.counter("usuarios.operacoes.chamadas", "operacao", "deletar_por_nome").increment();
	        Optional<User> userExist = userRepository.findByUserName(userName);

	        if (userExist.isPresent()) {
	            userRepository.delete(userExist.get());
	            logger.info("Usuário deletado com sucesso.", userName);
	            meterRegistry.counter("usuarios.delete.resultado", "status", "sucesso").increment();return true;
	        } else {
	            logger.warn("Usuário com nome {} não encontrado para exclusão.", userName);
	            meterRegistry.counter("usuarios.delete.resultado", "status", "nao_encontrado").increment();
				return false;
	        }
	    }
	}