package com.example.commerce.services;

import com.example.commerce.dtos.UserRequestDTO;
import com.example.commerce.dtos.UserResponseDTO;
import com.example.commerce.entities.User;
import com.example.commerce.exceptions.ResourceNotFoundException;
import com.example.commerce.mappers.UserMapper;
import com.example.commerce.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> getAllUsers(String sort) {
        if(!List.of("name", "email").contains(sort)) {
            sort = "name";
        }
        logger.debug("Fetching all users from database");
        List<UserResponseDTO> users = userRepository.findAll(Sort.by(sort))
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} users", users.size());
        return users;
    }

    public UserResponseDTO getUserById(Long id) {
        logger.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        logger.debug("Creating new user with email: {}", userRequestDTO.getEmail());
        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        logger.debug("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userRequestDTO.getName() != null && !userRequestDTO.getName().isEmpty()) {
            user.setName(userRequestDTO.getName());
        }
        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().isEmpty()) {
            user.setEmail(userRequestDTO.getEmail());
        }
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            user.setPassword(userRequestDTO.getPassword());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with id: {}", id);
        return userMapper.toResponseDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        logger.debug("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
        logger.info("User deleted successfully with id: {}", id);
    }
}
