package com.clubhub.entities;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id; // database ID, used in URLs, never shown to users
    // The WSU ID the student enters, String because it can start with 0 and we never do math on it
    @Column(unique = true, nullable = false)
    private String studentNumber;
    private String firstName;
    private String lastName;
    // The student signs in with this (FR-010-03), so it must be unique, required and stored in lowercase
    @Column(unique = true, nullable = false)
    private String email;
    private String major; // String for now, may become a Major entity later

    // Optional profile fields, can be null
    // EnumType.STRING stores the name ("WOMAN"), not the position, so reordering the enum is safe
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Pronouns pronouns;

    // Always a BCrypt hash, never the typed password (FR-010-02): StudentController hashes new ones
    // and PasswordMigration hashes any old plain-text ones at startup
    @JsonIgnore
    private String password;

    // Potential Fields
    // List of Joined Clubs
    // // date birth -> age -> DOB

    // Usage of default constructor  Java persistence API (JPA)
    protected Student() {}

    // No id parameter, the database generates it
    public Student(String studentNumber, String firstName, String lastName, String email, String major,
                   Gender gender, Pronouns pronouns, String password) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.gender = gender;
        this.pronouns = pronouns;
        this.password = password;
        // List of clubs the student is a member of -> would need to update resource controller and record
    }

    // Returns the database ID (not the WSU ID)
    public Long getId() {
        return id;
    }

    // Returns the student's WSU ID, shown on the profile page
    public String getStudentNumber() {
        return studentNumber;
    }

    // Returns the student's first name
    public String getFirstName() {
        return firstName;
    }

    // Returns the student's last name
    public String getLastName() {
        return lastName;
    }

    // Returns the student's email address
    public String getEmail() {
        return email;
    }

    // Returns the student's major
    public String getMajor() {
        return major;
    }

    // Returns the student's gender, may be null
    public Gender getGender() {
        return gender;
    }

    // Returns the student's pronouns, may be null
    public Pronouns getPronouns() {
        return pronouns;
    }

    // Returns the stored BCrypt hash, only Spring Security and PasswordMigration should read it
    public String getPassword() {
        return password;
    }

    // Replaces the stored password with a BCrypt hash, used by PasswordMigration (never pass a typed password)
    public void replacePasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

    // The one way emails are stored and looked up: trimmed and lowercase, so "Alex.Rivera@WSU.edu" matches
    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    // Potentially function to change name
}
