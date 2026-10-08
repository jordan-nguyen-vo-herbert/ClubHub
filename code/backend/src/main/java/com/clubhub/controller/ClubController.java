package com.clubhub.controller;

import com.clubhub.dto.ClubForm;
import com.clubhub.dto.ClubResponse;
import com.clubhub.dto.MemberResponse;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.Member;
import com.clubhub.entities.Student; // for the founder lookup in clubFormSubmit
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        model.addAttribute("club", toClubResponse(match)); // available in the template as ${club}
        return "club"; // renders templates/club.html
    }

    // Copies a Club entity into the DTO, shared by the club page and ClubSearch
    private ClubResponse toClubResponse(Club club) {
        // copy each membership into a DTO, so the template never sees the Student entity (password)
        List<MemberResponse> members = new ArrayList<>();
        for (Member m : club.getMembers()) {
            members.add(new MemberResponse(m.getStudent().getId(), m.getStudent().getFirstName(),
                    m.getStudent().getLastName(), club.getClubID(), m.getRole()));
        }
        return new ClubResponse(club.getClubID(), club.getClubName(), club.getAreaOfInterest(),
                club.getStatus(), members);
    }

    @GetMapping("/clubForm")
    public String clubForm(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        return "clubForm";
    }
    
    @PostMapping("/clubForm")
    public String clubFormSubmit(@ModelAttribute ClubForm newForm) {
        // No student with that WSU ID -> HTTP 400 instead of a generic 500
        Student founder = studentRepository.findByStudentNumber(newForm.getFounderStudentNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No student with that WSU ID"));
        Club club = new Club(newForm.getClubName(), newForm.getAreaOfInterest(), newForm.getDescription(), founder);
        club.addMember(founder, ClubRole.PRESIDENT); // founder is the first member, saved along with the club (cascade)
        Club saved = clubRepository.save(club); // database assigns the clubID
        return "redirect:/clubs/" + saved.getClubID(); // browser loads the new club's page
    }

    // FR: ClubSearch, lets students search for clubs by name or area of interest

    @GetMapping("/clubs/search/name") // GET: e.g. /clubs/search/name?name=chess
    public String searchByName(@RequestParam String name, Model model) { // name comes from ?name=... in the URL
        List<Club> found = clubRepository.findByClubNameContainingIgnoreCase(name); // matching Club entities
        List<ClubResponse> clubs = new ArrayList<>();
        for (Club club : found) {
            clubs.add(toClubResponse(club)); // convert each Club to a DTO (no Student entities/passwords)
        }
        model.addAttribute("clubs", clubs); // available in the template as ${clubs}
        return "clubs"; // renders templates/clubs.html (Yohann)
    }

    @GetMapping("/clubs/search/area") // GET: e.g. /clubs/search/area?area=BUSINESS
    public String searchByArea(@RequestParam AreaOfInterest area, Model model) { // Spring converts "BUSINESS" to AreaOfInterest.BUSINESS
        List<Club> found = clubRepository.findByAreaOfInterest(area); // matching Club entities
        List<ClubResponse> clubs = new ArrayList<>();
        for (Club club : found) { // for every club found, add to result
            clubs.add(toClubResponse(club)); // convert each Club to a DTO (no Student entities/passwords)
        }
        model.addAttribute("clubs", clubs); // available in the template as ${clubs}
        return "clubs"; // renders templates/clubs.html (Yohann)
    }
}
