package com.clubhub.dto;
import java.util.List;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.ClubStatus;

// What the club page and search results can see, built by ClubController.toClubResponse
public record ClubResponse (
    Long clubID,
    String clubName,
    AreaOfInterest areaOfInterest,
    ClubStatus status,
    List<MemberResponse> members
) {}
