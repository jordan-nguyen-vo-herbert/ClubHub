package com.clubhub.dto;
import java.util.List;
import com.clubhub.entities.Member;

public record ClubResponse (
    Long clubID,
    String clubname,
    List<Member> members
) {}
