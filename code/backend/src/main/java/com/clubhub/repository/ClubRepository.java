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
}
