package com.securetaskapp.services;

import com.securetaskapp.dto.AuthResponse;
import com.securetaskapp.dto.LoginRequest;
import com.securetaskapp.dto.RegisterRequest;
import com.securetaskapp.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser();
}
