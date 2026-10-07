package com.clubhub.dto;
import java.util.List;

public record ClubResponse (
    Long clubID,
    String clubName,
    List<MemberResponse> members
) {}
