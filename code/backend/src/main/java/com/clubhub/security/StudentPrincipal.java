package com.clubhub.security;

import java.util.Collection;
import java.util.List;

import com.clubhub.entities.Student;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// The signed-in student as Spring Security sees them, stored in the session after sign-in
// Copies the few fields it needs instead of holding the Student entity, so the session stays small
public class StudentPrincipal implements UserDetails, CredentialsContainer {
    private final Long studentId;
    private final String email;
    private final String firstName;
    private String passwordHash; // not final: eraseCredentials() clears it once sign-in is checked

    public StudentPrincipal(Student student) {
        this.studentId = student.getId();
        this.email = student.getEmail();
        this.firstName = student.getFirstName();
        this.passwordHash = student.getPassword();
    }

    // Database ID of the signed-in student, used for "My student profile" (FR-010-09)
    public Long getStudentId() {
        return studentId;
    }

    // Shown in the header with sec:authentication="principal.firstName" (FR-010-11)
    public String getFirstName() {
        return firstName;
    }

    // Every signed-in student is a ROLE_STUDENT, FR-009 adds administrators and officers later
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    // Spring compares the typed password against this BCrypt hash at sign-in
    @Override
    public String getPassword() {
        return passwordHash;
    }

    // Spring calls it the "username", for ClubHub it's the WSU email
    @Override
    public String getUsername() {
        return email;
    }

    // Spring calls this after a successful sign-in, so the hash never sits in the session (NFR-010-01)
    @Override
    public void eraseCredentials() {
        passwordHash = null;
    }

    // No expiring, locking or disabling of profiles yet, so every profile is always usable
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
