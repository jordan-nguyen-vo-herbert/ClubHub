package com.controller; // need to figure this out

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// /clubs/{clubID}
@ResponseController
public class ClubController {

    // need a method that tells Springboot that this is the method associated with GET request
    @GetMapping("/clubs/{clubID}")
    public ClubResponse getClubResponse (@Pathvariable Long clubID) {
        // now we need to find it 
        Club querey = ClubRepository.findByID(clubID);
        return new ClubResponse(querey.clubID(), querey.clubName(), querey.members());
    }
}
