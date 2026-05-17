package com.courseRegistration.studentRegistration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@MappedSuperclass  // This tells JPA to include these fields in child classes
public abstract class BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public BaseUser() {}

    public BaseUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Abstract methods for polymorphism
    public abstract String getRole();
    public abstract String getDisplayInfo();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
