package com.clubhub.dto;
import com.clubhub.entities.ClubRole;

// Only the student's ID and name, not the whole Student entity (which has the password)
public record MemberResponse(
    Long studentID, // the Student's database ID (Student.getId()), not the WSU ID
    String firstName,
    String lastName,
    Long clubID,
    ClubRole role
){}
