package com.clubhub.controller;

import com.clubhub.dto.ClubForm;
import com.clubhub.dto.ClubResponse;
import com.clubhub.dto.MemberResponse;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.Member;
import com.clubhub.entities.Student; // for the founder, the signed-in student
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// /clubs/{clubID}
@Controller // Tells Spring that this handles club-related requests and returns Thymeleaf pages
public class ClubController {
    private final ClubRepository clubRepository;
    private final StudentRepository studentRepository; // to load the signed-in student, a new club's founder

    public ClubController(ClubRepository clubRepository, StudentRepository studentRepository) { // Spring passes in both repositories automatically
        this.clubRepository = clubRepository;
        this.studentRepository = studentRepository;
    }

    // Dropdown options for the club form and the area search, available in the template as ${areaOptions}
    // Spring runs this before every request in this controller, so the list is there even when a form is shown again with errors
    @ModelAttribute("areaOptions")
    public AreaOfInterest[] areaOptions() {
        return AreaOfInterest.values(); // every area, in the order they're declared in the enum
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
                club.getDescription(), club.getMeetingInfo(), club.getFacultyAdvisor(), club.getContactEmail(),
                club.getSubmittedAt(), club.getStatus(), members);
    }

    /**
     * Checks a club registration on the server, so a request that skips the browser's checks is still refused
     * (FR-002.6). Each problem is attached to its own field, so the form shows it next to that field.
     * An area of interest that isn't in the list never reaches this method as a value: Spring records it as a
     * conversion error, whose message is in messages.properties.
     *
     * @param form   what the student submitted
     * @param result where field errors are recorded
     */
    private void checkRegistration(ClubForm form, BindingResult result) {
        String clubName = trimmed(form.getClubName());
        if (clubName.isEmpty()) {
            result.rejectValue("clubName", "required", "Enter a club name");
        } else if (clubRepository.existsByClubNameIgnoreCase(clubName)) {
            result.rejectValue("clubName", "duplicate", "A club with this name already exists");
        }

        if (form.getAreaOfInterest() == null && !result.hasFieldErrors("areaOfInterest")) {
            result.rejectValue("areaOfInterest", "required", "Choose an area of interest from the list");
        }

        if (trimmed(form.getDescription()).isEmpty()) {
            result.rejectValue("description", "required", "Enter a description");
        }

        String contactEmail = trimmed(form.getContactEmail());
        if (contactEmail.isEmpty()) {
            result.rejectValue("contactEmail", "required", "Enter a contact email");
        } else if (!contactEmail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            result.rejectValue("contactEmail", "invalid", "Enter a valid email, like name@wsu.edu");
        }
    }

    /**
     * Returns the value without surrounding spaces, or an empty string for a missing value.
     *
     * @param value a submitted form value, may be null
     * @return the trimmed value, never null
     */
    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Returns the trimmed value, or null when it is blank, so an optional field left empty is stored as null.
     *
     * @param value a submitted form value, may be null
     * @return the trimmed value, or null
     */
    private static String blankToNull(String value) {
        String trimmedValue = trimmed(value);
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    // The Student behind the signed-in user, the only place a founder comes from, never a value the browser sends (NFR-002-01)
    // SecurityConfig already turned signed-out visitors away, so this only fails if the profile is missing:
    // HTTP 403 instead of saving a club without a founder
    private Student signedInStudent(StudentPrincipal principal) {
        return Optional.ofNullable(principal)
                .map(StudentPrincipal::getStudentId)
                .flatMap(studentRepository::findById)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No profile for the signed-in student"));
    }

    // The founding officer on the form, for when it first opens and when it's shown again with errors (FR-002-02)
    private void addFounder(Model model, Student founder) {
        model.addAttribute("founderName", founder.getFirstName() + " " + founder.getLastName());
        model.addAttribute("founderEmail", founder.getEmail());
    }

    @GetMapping("/clubForm")
    public String clubForm(@AuthenticationPrincipal StudentPrincipal principal, Model model) { // Spring passes in the signed-in student
        addFounder(model, signedInStudent(principal));
        model.addAttribute("clubForm", new ClubForm());
        return "clubForm";
    }

    @PostMapping("/clubForm")
    public String clubFormSubmit(@ModelAttribute ClubForm newForm, BindingResult result, // result holds form errors, must come right after the form
            @AuthenticationPrincipal StudentPrincipal principal, Model model, RedirectAttributes redirectAttributes) {
        Student founder = signedInStudent(principal);
        checkRegistration(newForm, result);
        // Any error (a value Spring couldn't convert, or a check in checkRegistration) -> show the form again with what they typed
        if (result.hasErrors()) {
            addFounder(model, founder);
            return "clubForm";
        }
        Club club = new Club(trimmed(newForm.getClubName()), newForm.getAreaOfInterest(), newForm.getDescription(),
                blankToNull(newForm.getMeetingInfo()), blankToNull(newForm.getFacultyAdvisor()),
                trimmed(newForm.getContactEmail()), founder);
        club.addMember(founder, ClubRole.PRESIDENT); // founder is the first member, saved along with the club (cascade)
        Club saved = clubRepository.save(club); // database assigns the clubID
        redirectAttributes.addFlashAttribute("registered", true); // reaches the next page only, club.html shows the success message (FR-002-05)
        return "redirect:/clubs/" + saved.getClubID(); // browser loads the new club's page, so a refresh can't submit the form again
    }

    // My Club Requests, the clubs the signed-in student founded (FR-002-07)
    @GetMapping("/clubs/mine") // an exact path, so Spring picks this over /clubs/{clubID}
    public String myClubs(@AuthenticationPrincipal StudentPrincipal principal, Model model) {
        List<Club> found = clubRepository.findByFounderIdOrderByClubIDDesc(signedInStudent(principal).getId()); // only their own (NFR-002-02)
        List<ClubResponse> clubs = new ArrayList<>();
        for (Club club : found) {
            clubs.add(toClubResponse(club)); // convert each Club to a DTO (no Student entities/passwords)
        }
        model.addAttribute("clubs", clubs); // available in the template as ${clubs}
        return "myClubs"; // renders templates/myClubs.html, a plain page that Daniel styles in FR-002.8
    }

    // FR: ClubSearch, lets students search for clubs by name or area of interest

    @GetMapping("/clubs/search") // GET: the search page before any search, so the forms and area dropdown have a page to live on
    public String searchPage() {
        return "clubs"; // renders templates/clubs.html, ${clubs} is missing until the user searches
    }

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
