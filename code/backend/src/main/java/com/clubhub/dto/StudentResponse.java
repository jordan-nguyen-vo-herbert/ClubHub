package com.clubhub.dto; // need to figure this out

import com.clubhub.entities.Gender;
import com.clubhub.entities.Pronouns;

// No password or WSU ID, this is what the profile page can see
public record StudentResponse(
    Long id, // database ID
    String firstName,
    String lastName,
    String email,
    String major,
    Gender gender,
    Pronouns pronouns
) {}
