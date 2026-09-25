package com.clubhub.repository;
import com.clubhub.entities.Club;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<Club, Long> {
    // return a specific club
    Optional<Club> findById(Long clubID);
    // return all clubs matching major
}
