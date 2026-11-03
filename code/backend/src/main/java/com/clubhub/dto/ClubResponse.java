package com.clubhub.dto;
import java.time.Instant;
import java.util.List;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.ClubStatus;

// What the club page and search results can see, built by ClubController.toClubResponse
// meetingInfo and facultyAdvisor may be null, submittedAt is null only for clubs saved before FR-002.6
public record ClubResponse (
    Long clubID,
    String clubName,
    AreaOfInterest areaOfInterest,
    String description,
    String meetingInfo,
    String facultyAdvisor,
    String contactEmail,
    Instant submittedAt,
    ClubStatus status,
    List<MemberResponse> members
) {}
