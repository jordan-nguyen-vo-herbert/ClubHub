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
    // FR-002.6: meetingInfo and facultyAdvisor are optional, contactEmail is required
    private String meetingInfo;
    private String facultyAdvisor;
    private String contactEmail;

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

    public void setMeetingInfo(String meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public void setFacultyAdvisor(String facultyAdvisor) {
        this.facultyAdvisor = facultyAdvisor;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
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

    public String getMeetingInfo() {
        return meetingInfo;
    }

    public String getFacultyAdvisor() {
        return facultyAdvisor;
    }

    public String getContactEmail() {
        return contactEmail;
    }
}
