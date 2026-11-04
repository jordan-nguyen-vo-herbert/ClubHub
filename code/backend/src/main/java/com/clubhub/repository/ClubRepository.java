package com.clubhub.repository;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Long> {
    // return a specific club
    Optional<Club> findById(Long clubID);
    // ClubSearch: Spring writes the query from the method name, names must match Club's fields
    List<Club> findByClubNameContainingIgnoreCase(String clubName); // partial match, "chess" finds "Chess Club"
    List<Club> findByAreaOfInterest(AreaOfInterest areaOfInterest);
    List<Club> findByClubNameContainingIgnoreCaseAndAreaOfInterest(String clubName, AreaOfInterest areaOfInterest);
    @Query("""
        SELECT c FROM Club c
        WHERE c.areaOfInterest = :area
        AND (
            LOWER(c.clubName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<Club> searchByKeywordAndArea(
        @Param("keyword") String keyword,
        @Param("area") AreaOfInterest area
    );
    // My Club Requests: the clubs a student founded, newest first (founder.id, sorted by clubID from high to low)
    List<Club> findByFounderIdOrderByClubIDDesc(Long founderId);
    @Query("""
        SELECT c FROM Club c
        WHERE (:keyword IS NULL OR :keyword = ''
        OR LOWER(c.clubName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:area IS NULL OR c.areaOfInterest = :area)
    """)
    List<Club> searchClubs(
        @Param("keyword") String keyword,
        @Param("area") AreaOfInterest area
    );
    // Registration: true if a club already has this name, ignoring capitalization (FR-002.6)
    boolean existsByClubNameIgnoreCase(String clubName);
}
