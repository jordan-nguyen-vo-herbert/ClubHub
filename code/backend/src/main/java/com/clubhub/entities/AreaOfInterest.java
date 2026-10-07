package com.clubhub.entities;

public enum AreaOfInterest {
    ACADEMIC("Academic"),
    ARTS("Arts"),
    CULTURAL("Cultural"),
    GAMING("Gaming"),
    PROFESSIONAL("Professional"),
    RECREATION("Recreation"),
    RELIGIOUS("Religious"),
    SERVICE("Service"),
    SPORTS("Sports"),
    TECHNOLOGY("Technology"),
    OTHER("Other");

    private final String areaDescription;

    AreaOfInterest(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getArea() {
        return this.areaDescription;
    }
}
