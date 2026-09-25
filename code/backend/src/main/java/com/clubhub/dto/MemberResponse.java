package com.clubhub.dto;
import com.clubhub.entities.ClubRole;

public record MemberResponse(
    Long studentID,
    Long clubID, // may need to change to clubID
    ClubRole role
){}
