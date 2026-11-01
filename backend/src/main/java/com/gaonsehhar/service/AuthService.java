package com.gaonsehhar.service;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.security.AuthDtos;
import com.gaonsehhar.security.AuthDtos.AuthResponse;
import com.gaonsehhar.security.AuthDtos.LoginRequest;
import com.gaonsehhar.security.AuthDtos.RegisterRequest;
import com.gaonsehhar.security.AuthDtos.UserResponse;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.gaonsehhar.security.AuthDtos.*;

@Service
public class AuthService implements AuthServiceContract {
  private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email=request.email().trim().toLowerCase();
    if(users.existsByEmailIgnoreCase(email)) throw new ApiException(HttpStatus.CONFLICT,"Email is already registered");
    Role role=request.role()==null ? Role.CUSTOMER : request.role();
    if(role==Role.ADMIN) throw new ApiException(HttpStatus.BAD_REQUEST,"Cannot self-register as ADMIN");
    User user=new User(request.name().trim(),email,encoder.encode(request.password()),request.phone(),request.village(),role);
    users.save(user); return token(user);
  }
  @Transactional(readOnly=true)
  public AuthResponse login(LoginRequest request) {
    String email=request.email().trim().toLowerCase();
    User user=users.findByEmailIgnoreCase(email).orElseThrow(() ->
      new ApiException(HttpStatus.NOT_FOUND,"No account found for this email. Please create an account first."));
    if(!encoder.matches(request.password(),user.getPasswordHash()))
      throw new ApiException(HttpStatus.UNAUTHORIZED,"Incorrect password. Please try again.");
    return token(user);
  }
  private AuthResponse token(User user) { return new AuthResponse(jwt.generateToken(user.getEmail(),user.getRole().name()), new UserResponse(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getVillage(),user.getRole())); }
}
