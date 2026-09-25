package com.clubhub.controller; // need to figure this out
import com.clubhub.dto.ClubResponse; // need to figure this out
import com.clubhub.dto.StudentResponse;
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
        Optional<Club> query = clubRepository.findById(clubID); // optional in case no results
        if (!query.isPresent()) {
            query.orElseThrow(); // this will need to be fixed, returns IllegalArgumentException and will break
        }
        Club match = query.get();
        return new ClubResponse(match.getClubID(), match.getClubName(), match.getMembers());
    }
}
