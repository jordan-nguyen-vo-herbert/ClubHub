package com.clubhub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String major; // String for now, may become a Major entity later

    // Optional profile fields, can be null
    // EnumType.STRING stores the name ("WOMAN"), not the position, so reordering the enum is safe
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Pronouns pronouns;

    // TODO: hash before storing (e.g. BCrypt) once Spring Security is added
    @JsonIgnore
    private String password;

    // Potential Fields
    // List of Joined Clubs
    // // date birth -> age -> DOB

    // Usage of default constructor  Java persistence API (JPA)
    protected Student() {}

    // No id parameter, the database generates it
    public Student(String firstName, String lastName, String email, String major,
                   Gender gender, Pronouns pronouns, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.gender = gender;
        this.pronouns = pronouns;
        this.password = password;
        // List of clubs the student is a member of -> would need to update resource controller and record
    }

    // Returns the student's ID
    public Long getStudentID() {
        return id;
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

    // Returns the student's password for security reasons, this might be hashed
    public String getPassword() {
        return password;
    }

    // Potentially function to change name
}
