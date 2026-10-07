package com.clubhub.entities;

// Where a club application is in the approval process
public enum ClubStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String statusDescription;

    ClubStatus(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatus() {
        return this.statusDescription;
    }
}
