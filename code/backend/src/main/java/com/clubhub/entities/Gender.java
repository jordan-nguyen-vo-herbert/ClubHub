package com.clubhub.entities;

public enum Gender {
    WOMAN("Woman"),
    MAN("Man"),
    NONBINARY("Non-binary"),
    SELF_DESCRIBE("Self-describe"),
    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String genderDescription;

    Gender(String genderDescription) {
        this.genderDescription = genderDescription;
    }

    public String getGender() {
        return this.genderDescription;
    }
}
