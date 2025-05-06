package com.example.springbootmfa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "password_hashed", nullable = false)
    private String passwordHashed;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "mfa_secret")
    private String mfaSecret;
    
    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;
}
