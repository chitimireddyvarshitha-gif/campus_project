package com.campusevent.management.service;

import com.campusevent.management.dto.request.LoginRequest;
import com.campusevent.management.dto.request.RegisterRequest;
import com.campusevent.management.dto.response.AuthResponse;
import com.campusevent.management.dto.response.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);
}
