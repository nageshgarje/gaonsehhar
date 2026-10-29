package com.gaonsehhar.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gaonsehhar.security.AuthDtos;
import com.gaonsehhar.security.AuthDtos.AuthResponse;
import com.gaonsehhar.security.AuthDtos.LoginRequest;
import com.gaonsehhar.security.AuthDtos.RegisterRequest;
import com.gaonsehhar.service.AuthServiceContract;

import static com.gaonsehhar.security.AuthDtos.*;

@RestController @RequestMapping({"/api/auth", "/api/v1/auth"})
public class AuthController {
  private final AuthServiceContract service;
  public AuthController(AuthServiceContract service){this.service=service;}
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request){return service.register(request);}
  @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request){return service.login(request);}
}
//