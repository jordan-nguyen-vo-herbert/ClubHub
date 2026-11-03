package com.clubhub.repository;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Long> {
    // return a specific club
    Optional<Club> findById(Long clubID);
    // ClubSearch: Spring writes the query from the method name, names must match Club's fields
    List<Club> findByClubNameContainingIgnoreCase(String clubName); // partial match, "chess" finds "Chess Club"
    List<Club> findByAreaOfInterest(AreaOfInterest areaOfInterest);
    // My Club Requests: the clubs a student founded, newest first (founder.id, sorted by clubID from high to low)
    List<Club> findByFounderIdOrderByClubIDDesc(Long founderId);
    // Registration: true if a club already has this name, ignoring capitalization (FR-002.6)
    boolean existsByClubNameIgnoreCase(String clubName);
}
