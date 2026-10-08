package com.campusevent.management.service.impl;

import com.campusevent.management.dto.request.LoginRequest;
import com.campusevent.management.dto.request.RegisterRequest;
import com.campusevent.management.dto.response.AuthResponse;
import com.campusevent.management.dto.response.UserResponse;
import com.campusevent.management.entity.Role;
import com.campusevent.management.entity.User;
import com.campusevent.management.exception.BadRequestException;
import com.campusevent.management.exception.DuplicateResourceException;
import com.campusevent.management.exception.ResourceNotFoundException;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.security.JwtTokenProvider;
import com.campusevent.management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " is already registered.");
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.STUDENT;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(assignedRole)
                .department(request.getDepartment() != null ? request.getDepartment() : "General Studies")
                .year(request.getYear() != null ? request.getYear() : "1st Year (Freshman)")
                .designation(request.getDesignation())
                .avatar(assignedRole == Role.STUDENT
                        ? "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300"
                        : "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300")
                .build();

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cleanEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + cleanEmail));

        String token = tokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.fromEntity(user);
    }
}
