package com.gaonsehhar.config;

import com.gaonsehhar.security.JwtAuthFilter;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtFilter;
  @Value("${app.cors.origins}") private String corsOrigins;
  public SecurityConfig(JwtAuthFilter jwtFilter) { this.jwtFilter=jwtFilter; }
  @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).cors(cors -> {}).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/api/v1/auth/**", "/actuator/health").permitAll().requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**", "/api/v1/products", "/api/v1/products/**").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(Arrays.asList(corsOrigins.split(","))); c.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));     c.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Idempotency-Key"));
    UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",c); return source;
  }
}
