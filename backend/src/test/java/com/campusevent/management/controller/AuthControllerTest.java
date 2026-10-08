package com.campusevent.management.controller;

import com.campusevent.management.dto.request.LoginRequest;
import com.campusevent.management.dto.response.AuthResponse;
import com.campusevent.management.dto.response.UserResponse;
import com.campusevent.management.security.JwtAuthenticationEntryPoint;
import com.campusevent.management.security.JwtTokenProvider;
import com.campusevent.management.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void login_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("varshitha.c@campus.edu")
                .password("CampusPass@2026")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Varshitha Chitimireddy")
                .email("varshitha.c@campus.edu")
                .role("STUDENT")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("mock-jwt-token")
                .user(userResponse)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.user.name").value("Varshitha Chitimireddy"))
                .andExpect(jsonPath("$.user.role").value("STUDENT"));
    }
}
