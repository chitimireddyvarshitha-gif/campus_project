package com.campusevent.management.controller;

import com.campusevent.management.dto.request.LoginRequest;
import com.campusevent.management.dto.request.RegisterRequest;
import com.campusevent.management.dto.response.AuthResponse;
import com.campusevent.management.dto.response.UserResponse;
import com.campusevent.management.entity.Role;
import com.campusevent.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/student/register")
    public ResponseEntity<AuthResponse> registerStudent(@Valid @RequestBody RegisterRequest request) {
        request.setRole(Role.STUDENT);
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/student/login")
    public ResponseEntity<AuthResponse> studentLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/faculty/login")
    public ResponseEntity<AuthResponse> facultyLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse user = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(user);
    }
}
