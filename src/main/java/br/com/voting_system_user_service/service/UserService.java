package br.com.voting_system_user_service.service;

import br.com.voting_system_user_service.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
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
	public List<UserDTO> getAllUsers() {
		logger.info("Recebida requisição para listar todos os usuários.");
        meterRegistry.counter("usuario.listar.todas.chamadas").increment();
        
        long start = System.currentTimeMillis();
        List<User> users = userRepository.findAll();
        long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.listar.todas.tempo").record(end - start, TimeUnit.MILLISECONDS);
        
        if (users.isEmpty()) {
        	logger.warn("Lista de usuários retornou vazia.");
            meterRegistry.counter("usuario.listar.todas.vazio").increment();
        } else {
        	 logger.info(" {} usuários encontrados", users.size());
	         meterRegistry.counter("usuario.listar.todas.sucesso").increment();
	         meterRegistry.gauge("usuario.listar.todas.quantidade", users, List::size);
        } 
        
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
	}
	
	
	
	public Optional<UserDTO> getUserById(Long id) {
		logger.info("Recebida requisição para buscar usuário com ID {}", id);
        meterRegistry.counter("usuario.buscar.id.chamadas").increment();

        long start = System.currentTimeMillis();     
		Optional<User> user = userRepository.findById(id);
		long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.buscar.id.tempo").record(end - start, TimeUnit.MILLISECONDS);
        
        if (user.isPresent()) {
        	logger.info("Usuário encontrado ",  user.get());
            meterRegistry.counter("usuario.buscar.id.sucesso").increment();
        } else {
        	logger.warn("Usuário com ID {} não encontrado.", id);
            meterRegistry.counter("usuario.buscar.id.naoencontrado").increment();
        }
        
        return user.map(UserDTO::new);
	}
	
	
	 @PreAuthorize("hasRole('ADMIN')")
	 public Optional<UserDTO> getUserByName(String userName) {
	        logger.info("Recebida requisição para buscar usuário com Nome {}", userName);
	        meterRegistry.counter("usuario.buscar.nome.chamadas").increment();

	        long start = System.currentTimeMillis();
	        Optional<User> user = userRepository.findByUserName(userName);
	        long end = System.currentTimeMillis();
	        meterRegistry.timer("usuario.buscar.nome.tempo").record(end - start, TimeUnit.MILLISECONDS);

	        if (user.isPresent()) {
	            logger.info("Usuário encontrado {}", user.get());
	            meterRegistry.counter("usuario.buscar.nome.sucesso").increment();
	        } else {
	            logger.warn("Usuário com nome {} não encontrado.", userName);
	            meterRegistry.counter("usuario.buscar.nome.naoencontrado").increment();
	        }

	        return user.map(UserDTO::new);
	    }
	
	 @PreAuthorize("hasRole('ADMIN')")
	 public boolean deleteUserById(Long id) {
	        logger.info("Recebida requisição para deletar usuário com ID {}", id);
	        meterRegistry.counter("usuario.deletar.id.chamadas").increment();

	        long start = System.currentTimeMillis();
	        Optional<User> userExist = userRepository.findById(id);
	        
	        if (userExist.isPresent()) {
	            userRepository.deleteById(id);
	            logger.info("Usuário deletado com sucesso.");
	            meterRegistry.counter("usuario.deletar.id.sucesso").increment();
	            meterRegistry.timer("usuario.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	            return true;
	        } else {
	            logger.warn("Usuário com ID {} não encontrado para exclusão.", id);
	            meterRegistry.counter("usuario.deletar.id.naoencontrado").increment();
	            meterRegistry.timer("usuario.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	            return false;
	        }
	    }
	 
	 
	 @PreAuthorize("hasRole('ADMIN')")
	 public boolean deleteUserByName(String userName) {
	        logger.info("Recebida requisição para deletar usuário com nome {}", userName);
	        meterRegistry.counter("usuario.deletar.nome.chamadas").increment();

	        long start = System.currentTimeMillis();
	        Optional<User> userExist = userRepository.findByUserName(userName);

	        if (userExist.isPresent()) {
	            userRepository.delete(userExist.get());
	            logger.info("Usuário deletado com sucesso.", userName);
	            meterRegistry.counter("usuario.deletar.nome.sucesso").increment();
	            meterRegistry.timer("usuario.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	            return true;
	        } else {
	            logger.warn("Usuário com nome {} não encontrado para exclusão.", userName);
	            meterRegistry.counter("usuario.deletar.nome.naoencontrado").increment();
	            meterRegistry.timer("usuario.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	            return false;
	        }
	    }
	}