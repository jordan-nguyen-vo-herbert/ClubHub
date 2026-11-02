package com.clubhub.dto;

import com.clubhub.entities.AreaOfInterest;

// This class takes input from a student applying to start a new club
// No constructor, Spring uses Java's default one (new ClubForm()) then calls the setters
// Not included: clubID (database generates it), status and dateApproved (set by the approval process),
// members (added with Club.addMember), and the founder: it's always the signed-in student, so a value
// sent with the form has nowhere to go (NFR-002-01)
public class ClubForm {
    private String clubName;
    private AreaOfInterest areaOfInterest;
    private String description;

    // Setters

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public void setAreaOfInterest(AreaOfInterest areaOfInterest) {
        this.areaOfInterest = areaOfInterest;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getters

    public String getClubName() {
        return clubName;
    }

    public AreaOfInterest getAreaOfInterest() {
        return areaOfInterest;
    }

    public String getDescription() {
        return description;
    }
}
