package com.gaonsehhar.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

import com.gaonsehhar.service.AuthService;
import com.gaonsehhar.service.JwtService;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static com.gaonsehhar.security.AuthDtos.*;

class AuthServiceTest {
 @Test void rejectsDuplicateEmail() {
   UserRepository users=mock(UserRepository.class); when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);
   AuthService service=new AuthService(users,new BCryptPasswordEncoder(),mock(JwtService.class));
   assertThrows(com.gaonsehhar.common.ApiException.class,()->service.register(new RegisterRequest("A","a@b.com","password1",null,null,Role.CUSTOMER)));
 }
 @Test void authenticatesValidPassword() {
   UserRepository users=mock(UserRepository.class); var encoder=new BCryptPasswordEncoder();
   User user=new User("A","a@b.com",encoder.encode("password1"),null,null,Role.CUSTOMER);
   when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user)); JwtService jwt=mock(JwtService.class); when(jwt.generateToken(anyString(),anyString())).thenReturn("signed-token");
   assertEquals("signed-token",new AuthService(users,encoder,jwt).login(new LoginRequest("a@b.com","password1")).token());
 }
}
