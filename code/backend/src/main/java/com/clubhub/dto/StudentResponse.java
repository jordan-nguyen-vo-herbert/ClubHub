package main.java.com.clubhub.entities; // might need to check the path on this one

public record StudentResponse(
    Long id,
    String name,
    String email
) {}
