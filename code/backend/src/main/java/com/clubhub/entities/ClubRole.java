package com.clubhub.entities;

public enum ClubRole {
    PRESIDENT("President"),
    VICEPRESIDENT("Vice President"),
    TREASURER("Treasurer"),
    SECRETARY("Secretary"),
    REGULARMEMBER("Regular Member");
   
    private final String roleDescription;
    
    ClubRole(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getRole() {
        return this.roleDescription;
    }
}
