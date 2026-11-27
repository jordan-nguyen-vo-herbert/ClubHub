package com.clubhub.SeleniumTests;

public class StudentTest {
    private String studentNumber;
    private String firstName;
    private String lastName;
    // The student signs in with this (FR-010-03), so it must be unique, required and stored in lowercase
    private String email;
    private String major; 
    private String password;

    public StudentTest(String studentNumber, String firstName, String lastName, String email, String major, String password) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.password = password;
    }

    public String getStudentNumber () {
        return this.studentNumber;
    }

    public String getFirstName () {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail () {
        return this.email;
    }

    public String getMajor () {
        return this.major;
    }

    public String getPassword () {
        return this.password;
    }
}
