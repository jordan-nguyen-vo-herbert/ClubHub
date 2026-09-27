package com.clubhub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String lastName;
    private String email;
    // will need enum for major
    private String password; 

    // Potential Fields
    // Gender
    // List of Joined Clubs
    // // date birth -> age -> DOB

    // Usage of default constructor  Java persistence API (JPA)
    protected Student() {}

    public Student(Long id, String lastName, String email, String password) {
        this.id = id;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        // List of clubs the student is a member of -> would need to update resource controller and record
    }

    // Returns the student's ID
    public Long getStudentID() {
        return id;
    }

    // Returns the student's last name
    public String getLastName() {
        return lastName;
    }

    // Returns the student's email address
    public String getEmail() {
        return email;
    }

    // Returns the student's password for security reasons, this might be hashed
    public String getPassword() {
        return password;
    }

    // Potentially function to change name
}