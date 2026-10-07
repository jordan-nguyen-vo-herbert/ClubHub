package com.clubhub.controller;

import com.clubhub.dto.ClubForm;
import com.clubhub.dto.ClubResponse;
import com.clubhub.dto.MemberResponse;
import com.clubhub.entities.Club;
import com.clubhub.entities.Student; // for the founder lookup in clubFormSubmit
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

// /clubs/{clubID}
@Controller // Tells Spring that this handles club-related requests and returns Thymeleaf pages
public class ClubController {
    private final ClubRepository clubRepository;
    private final StudentRepository studentRepository; // to look up a club's founder by WSU ID

    public ClubController(ClubRepository clubRepository, StudentRepository studentRepository) { // Spring passes in both repositories automatically
        this.clubRepository = clubRepository;
        this.studentRepository = studentRepository;
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

    @GetMapping("/clubForm")
    public String clubForm(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        return "clubForm";
    }
    
    @PostMapping("/clubForm")
    public String clubFormSubmit(@ModelAttribute ClubForm newForm, Model model) {
        model.addAttribute("clubForm", newForm);
        return "clubForm";
    }

    // FR: ClubSearch will be implemented by October 12th
    // Lets students search for clubs by name or area of interest
}
