package com.clubhub.controller; // need to figure this out
import com.clubhub.dto.ClubResponse; // need to figure this out
import com.clubhub.dto.MemberResponse;
import com.clubhub.repository.ClubRepository; // need to figure this out
import com.clubhub.entities.Club;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;


// /clubs/{clubID}
@Controller // Tells Spring that this handles club-related requests and returns Thymeleaf pages
public class ClubController {
    private ClubRepository clubRepository;
    
    public ClubController(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    // tells Springboot that this is the method associated with GET request
    @GetMapping("/clubs/{clubID}")
    public String clubPage(@PathVariable Long clubID, Model model) {
        // No match -> HTTP 404, Spring shows templates/error/404.html
        Club match = clubRepository.findById(clubID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));
        // copy each membership into a DTO, so the template never sees the Student entity (password)
        List<MemberResponse> members = match.getMembers().stream()
                .map(m -> new MemberResponse(m.getStudent().getId(), m.getStudent().getFirstName(),
                        m.getStudent().getLastName(), match.getClubID(), m.getRole()))
                .toList();
        ClubResponse club = new ClubResponse(match.getClubID(), match.getClubName(), members);
        model.addAttribute("club", club); // available in the template as ${club}
        return "club"; // renders templates/club.html
    }
}
