package com.clubhub.dto; // need to figure this out

import com.clubhub.entities.Gender;
import com.clubhub.entities.Pronouns;

// No password, this is what gets sent back to the client
public record StudentResponse(
    Long studentID,
    String firstName,
    String lastName,
    String email,
    String major,
    Gender gender,
    Pronouns pronouns
) {}
