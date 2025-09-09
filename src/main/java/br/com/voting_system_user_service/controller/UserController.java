package br.com.voting_system_user_service.controller;

import br.com.voting_system_user_service.dto.UserDTO;
import br.com.voting_system_user_service.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author fsdney
 */

//@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
//@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    //@Operation(summary = "Listar", description = "Método para listar todos os usuários")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //@Operation(summary = "Buscar Por Id", description = "Método para buscar um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //@Operation(summary = "Buscar Por Nome", description = "Método para buscar um usuário por Nome")
    @GetMapping("/userName/{userName}")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable("userName") String userName) {
        return userService.getUserByName(userName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //@Operation(summary = "Deletar Por Id", description = "Método para deletar um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        boolean removed = userService.deleteUserById(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado para exclusão.");
        }
    }

    //@Operation(summary = "Deletar Por Nome", description = "Método para deletar um usuário por Nome")
    @DeleteMapping("/userName/{userName}")
    public ResponseEntity<String> deleteUserByName(@PathVariable("userName") String userName) {
        boolean removed = userService.deleteUserByName(userName);
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado para exclusão.");
        }
    }
}  