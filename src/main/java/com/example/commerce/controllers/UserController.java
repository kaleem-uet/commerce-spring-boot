package com.example.commerce.controllers;

import com.example.commerce.dtos.UserRequestDTO;
import com.example.commerce.dtos.UserResponseDTO;
import com.example.commerce.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false,defaultValue = "", name = "sort") String sort
    ) {
        logger.info("GET /users - Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers(sort);
        logger.info("GET /users - Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        logger.info("GET /users/{} - Fetching user by id", id);
        UserResponseDTO user = userService.getUserById(id);
        logger.info("GET /users/{} - User found", id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /users - Creating new user with email: {}", userRequestDTO.getEmail());
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        logger.info("POST /users - User created with id: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("PUT /users/{} - Updating user", id);
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        logger.info("PUT /users/{} - User updated successfully", id);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> partialUpdateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("PATCH /users/{} - Partially updating user", id);
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        logger.info("PATCH /users/{} - User partially updated successfully", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /users/{} - Deleting user", id);
        userService.deleteUser(id);
        logger.info("DELETE /users/{} - User deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
