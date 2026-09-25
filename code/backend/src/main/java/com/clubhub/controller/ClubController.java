package com.clubhub.controller; // need to figure this out
import com.clubhub.dto.ClubResponse; // need to figure this out
import com.clubhub.repository.ClubRepository; // need to figure this out
import com.clubhub.entities.Club;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


// /clubs/{clubID}
@RestController
public class ClubController {
    private ClubRepository clubRepository;
    // need a method that tells Springboot that this is the method associated with GET request
    @GetMapping("/clubs/{clubID}")
    public ClubResponse getClubResponse (@PathVariable Long clubID) {
        // now we need to find it 
        Optional<Club> query = clubRepository.findById(clubID);
        return new ClubResponse(query.getClubID(), query.getClubName(), query.getMembers());
    }
}
