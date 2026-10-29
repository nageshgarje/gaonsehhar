package com.gaonsehhar.service;

import com.gaonsehhar.security.AuthDtos.AuthResponse;
import com.gaonsehhar.security.AuthDtos.LoginRequest;
import com.gaonsehhar.security.AuthDtos.RegisterRequest;

public interface AuthServiceContract {
  AuthResponse register(RegisterRequest request);
  AuthResponse login(LoginRequest request);
}
