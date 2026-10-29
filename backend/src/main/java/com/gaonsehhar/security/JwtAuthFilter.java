package com.gaonsehhar.security;

import com.gaonsehhar.service.JwtService;
import com.gaonsehhar.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService; private final UserRepository users;
  public JwtAuthFilter(JwtService jwtService, UserRepository users) { this.jwtService=jwtService; this.users=users; }
  @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String header=request.getHeader("Authorization");
    if(header != null && header.startsWith("Bearer ")) {
      try {
        String token=header.substring(7); var claims=jwtService.parse(token);
        users.findByEmailIgnoreCase(claims.getSubject()).ifPresent(user -> {
          var auth=new UsernamePasswordAuthenticationToken(user, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));
          SecurityContextHolder.getContext().setAuthentication(auth);
        });
      } catch (Exception ignored) { SecurityContextHolder.clearContext(); }
    }
    chain.doFilter(request,response);
  }
}
