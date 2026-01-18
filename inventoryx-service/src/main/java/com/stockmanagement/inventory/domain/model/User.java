package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.Email;
import com.stockmanagement.inventory.domain.model.valueobject.Password;
import com.stockmanagement.inventory.domain.model.valueobject.UserId;
import com.stockmanagement.inventory.domain.model.valueobject.Username;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class User {
    private final UserId id;
    private final Username username;
    private Email email; // non-final to allow updates
    private Password password;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private boolean isLocked;
    private int failedAttempts;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private final Set<Role> roles;

    public User(UserId id, Username username, Email email, Password password, String firstName, String lastName) {
        this(id, username, email, password, firstName, lastName, true, false, 0, null, LocalDateTime.now());
    }

    public User(UserId id, Username username, Email email, Password password, String firstName, String lastName,
            boolean isActive, boolean isLocked, int failedAttempts, LocalDateTime lastLoginAt,
            LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.isLocked = isLocked;
        this.failedAttempts = failedAttempts;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.roles = new HashSet<>();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void updateProfile(String firstName, String lastName, Email email) {
        if (firstName != null && !firstName.isBlank())
            this.firstName = firstName;
        if (lastName != null && !lastName.isBlank())
            this.lastName = lastName;
        if (email != null)
            this.email = email;
    }

    public void activate() {
        this.isActive = true;
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void loginSuccess() {
        this.failedAttempts = 0;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void loginFailed() {
        this.failedAttempts++;
        if (this.failedAttempts >= 5) {
            this.isLocked = true;
        }
    }
}
