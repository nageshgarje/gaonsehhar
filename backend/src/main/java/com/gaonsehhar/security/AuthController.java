package com.gaonsehhar.security;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.security.AuthDtos.*;

@RestController @RequestMapping({"/api/auth", "/api/v1/auth"})
public class AuthController {
  private final AuthService service;
  public AuthController(AuthService service){this.service=service;}
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request){return service.register(request);}
  @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request){return service.login(request);}
}
//