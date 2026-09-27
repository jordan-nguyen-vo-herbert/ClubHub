// Created a seperate Member class, so we only have to store ID number and club name, but might fine tune that later'
package com.clubhub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id 
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long studentID;
    private Long clubID; // may need to change to clubID
    private ClubRole role;
    // Might need status

    public Member(Long studentID, Long clubID, ClubRole role) {
        this.studentID = studentID;
        this.clubID = clubID;
        this.role = role;
    }

    public Long getStudentID() {
        return this.studentID;
    }

    public Long clubName() {
        return this.clubID;
    }

    public String getRole() {
        return this.role.getRole();
    }
}
