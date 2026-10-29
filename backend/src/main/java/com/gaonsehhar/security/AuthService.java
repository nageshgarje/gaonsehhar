package com.gaonsehhar.security;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.gaonsehhar.security.AuthDtos.*;

@Service
public class AuthService {
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
    User user=users.findByEmailIgnoreCase(request.email().trim().toLowerCase()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
    if(!encoder.matches(request.password(),user.getPasswordHash())) throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
    return token(user);
  }
  private AuthResponse token(User user) { return new AuthResponse(jwt.generateToken(user.getEmail(),user.getRole().name()), new UserResponse(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getVillage(),user.getRole())); }
}
