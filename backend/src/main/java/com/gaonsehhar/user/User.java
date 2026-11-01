package com.gaonsehhar.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(nullable=false, unique=true) private String email;
  @Column(name="password_hash", nullable=false) private String passwordHash;
  private String phone; private String village;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
  @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt = Instant.now();
  @Version @Column(nullable=false) private long version;
  protected User() {}
  public User(String name, String email, String passwordHash, String phone, String village, Role role) { this.name=name; this.email=email; this.passwordHash=passwordHash; this.phone=phone; this.village=village; this.role=role; }
  public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getPhone(){return phone;} public String getVillage(){return village;} public Role getRole(){return role;} public Instant getCreatedAt(){return createdAt;}
}
