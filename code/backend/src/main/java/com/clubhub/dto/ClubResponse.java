package com.clubhub.dto;

public record ClubResponse (
    Long clubID,
    String clubname,
    List<Member> members
) {}
