package com.example.commerce.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.commerce.dtos.AuthResponseDTO;
import com.example.commerce.dtos.LoginRequestDTO;
import com.example.commerce.dtos.MessageResposeDTO;
import com.example.commerce.dtos.RegisterRequestDTO;
import com.example.commerce.dtos.RegisterResponseDTO;
import com.example.commerce.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO authResponse = authService.register(request);
            RegisterResponseDTO response = RegisterResponseDTO.builder()
                    .message("User registered successfully")
                    .data(authResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResposeDTO(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResposeDTO("Invalid username or password"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<MessageResposeDTO> test() {
        return ResponseEntity.ok(new MessageResposeDTO("API is working!"));
    }
}
