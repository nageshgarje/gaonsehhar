package com.gaonsehhar.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
  Optional<User> findFirstByRole(Role role);
}
