package com.clubhub.dto;

import com.clubhub.entities.Gender;
import com.clubhub.entities.Pronouns;

// This class takes input from the user to populate profile relevant information
// No constructor, Spring uses Java's default one (new StudentForm()) then calls the setters
public class StudentForm {
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private Gender gender; // optional
    private Pronouns pronouns; // optional
    private String password;

    // Setters

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPronouns(Pronouns pronouns) {
        this.pronouns = pronouns;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    public Gender getGender() {
        return gender;
    }

    public Pronouns getPronouns() {
        return pronouns;
    }

    public String getPassword() {
        return password;
    }
}
