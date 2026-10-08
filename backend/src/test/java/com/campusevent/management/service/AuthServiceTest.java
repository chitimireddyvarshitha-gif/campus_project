package com.campusevent.management.service;

import com.campusevent.management.dto.request.LoginRequest;
import com.campusevent.management.dto.request.RegisterRequest;
import com.campusevent.management.dto.response.AuthResponse;
import com.campusevent.management.entity.Role;
import com.campusevent.management.entity.User;
import com.campusevent.management.exception.DuplicateResourceException;
import com.campusevent.management.repository.UserRepository;
import com.campusevent.management.security.JwtTokenProvider;
import com.campusevent.management.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Varshitha Chitimireddy")
                .email("varshitha.c@campus.edu")
                .password("encoded_pass")
                .phone("+15552348901")
                .role(Role.STUDENT)
                .department("Computer Science")
                .build();

        registerRequest = RegisterRequest.builder()
                .name("Varshitha Chitimireddy")
                .email("varshitha.c@campus.edu")
                .password("CampusPass@2026")
                .phone("+15552348901")
                .role(Role.STUDENT)
                .build();

        loginRequest = LoginRequest.builder()
                .email("varshitha.c@campus.edu")
                .password("CampusPass@2026")
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateToken(any(User.class))).thenReturn("mock_jwt_token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getToken());
        assertEquals("varshitha.c@campus.edu", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("varshitha.c@campus.edu")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("varshitha.c@campus.edu")).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateToken(sampleUser)).thenReturn("mock_jwt_token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getToken());
        assertEquals("Varshitha Chitimireddy", response.getUser().getName());
        assertEquals("STUDENT", response.getUser().getRole());
    }
}
