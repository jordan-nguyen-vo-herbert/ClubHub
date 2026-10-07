package com.clubhub.entities;

public enum Pronouns {
    HE_HIM("he/him"),
    SHE_HER("she/her"),
    THEY_THEM("they/them"),
    HE_THEY("he/they"),
    SHE_THEY("she/they"),
    ANY("any pronouns"),
    OTHER("other"),
    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String pronounsDescription;

    Pronouns(String pronounsDescription) {
        this.pronounsDescription = pronounsDescription;
    }

    public String getPronouns() {
        return this.pronounsDescription;
    }
}
