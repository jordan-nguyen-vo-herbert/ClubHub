// Created a seperate Member class, so we only have to store ID number and club name, but might fine tune that later'
package com.clubhub.entities;

public class Member {
    private Long studentID;
    private String clubName;
    // Might need roll

    public Member(Long studentID, String clubName) {
        this.studentID = studentID;
        this.clubName = clubName;
    }
}
