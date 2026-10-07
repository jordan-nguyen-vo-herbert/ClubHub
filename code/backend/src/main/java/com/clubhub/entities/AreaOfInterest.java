package com.clubhub.entities;

// What a club is about, academic areas follow WSU's colleges
public enum AreaOfInterest {
    // Academic (by college)
    ENGINEERING_ARCHITECTURE("Engineering & Architecture"),     // Voiland
    COMPUTER_SCIENCE("Computer Science"),                       // Voiland
    NATURAL_SCIENCES_MATH("Natural Sciences & Math"),           // Arts and Sciences
    HUMANITIES_SOCIAL_SCIENCES("Humanities & Social Sciences"), // Arts and Sciences
    FINE_PERFORMING_ARTS("Fine & Performing Arts"),             // Arts and Sciences
    AGRICULTURE_ENVIRONMENT("Agriculture & Environment"),       // CAHNRS
    BUSINESS("Business"),                                       // Carson
    COMMUNICATION_MEDIA("Communication & Media"),               // Murrow
    EDUCATION_SPORT_SCIENCE("Education & Sport Science"),       // Education, Sport and Human Sciences
    PRE_HEALTH("Pre-Health"),                                   // premed, pre-nursing, pre-pharmacy
    PRE_VET("Pre-Vet"),                                         // Veterinary Medicine

    // Non-academic
    CULTURAL("Cultural"),
    RELIGIOUS("Religious"),
    SERVICE("Service"),
    SPORTS_RECREATION("Sports & Recreation"),
    GAMING("Gaming"),
    OTHER("Other");

    private final String areaDescription;

    AreaOfInterest(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getArea() {
        return this.areaDescription;
    }
}
