package com.clubhub.ClubTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.ClubStatus;
import com.clubhub.entities.Member;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Specification-based tests for signed-in club registration, FR-002.7 (#50) and FR-002.8 (#58), one test per test
 * case in specs/features/club-registration/validation.md (TEST-012, #61).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClubRegistrationValidationTest {

    private static final String CONFIRMATION = "was submitted and is waiting for approval";
    private static final String MY_CLUB_REQUESTS_LINK = "href=\"/clubs/mine\"";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubRepository clubRepository;

    private final StudentData butch = SampleStudents.ALL.get(0);
    private final StudentData alex = SampleStudents.ALL.get(2);

    /**
     * Saves a student straight to the database, the way a created profile is stored.
     *
     * @param student the sample student to save
     * @return the saved student, with its database ID
     */
    private Student saveStudent(StudentData student) {
        return studentRepository.save(new Student(student.studentNumber(), student.firstName(), student.lastName(),
                student.email(), student.major(), null, null, student.password()));
    }

    /**
     * Builds a valid registration for this club name, with every required field filled in.
     *
     * @param clubName the club's name
     * @return the POST request, with its CSRF token, ready to sign in and perform
     */
    private MockHttpServletRequestBuilder validRegistration(String clubName) {
        return post("/clubForm").with(csrf())
                .param("clubName", clubName)
                .param("areaOfInterest", AreaOfInterest.SERVICE.name())
                .param("description", "A club for testing registration")
                .param("contactEmail", "club.contact@wsu.edu");
    }

    /**
     * Registers a club as this signed-in student.
     *
     * @param student  the signed-in student
     * @param clubName the club's name
     * @return the result, ready for expectations
     * @throws Exception if the request fails
     */
    private ResultActions register(Student student, String clubName) throws Exception {
        return mockMvc.perform(validRegistration(clubName).with(user(new StudentPrincipal(student))));
    }

    /**
     * Opens a page as this signed-in student.
     *
     * @param url     the page to open
     * @param student the signed-in student
     * @return the result, ready for expectations
     * @throws Exception if the request fails
     */
    private ResultActions open(String url, Student student) throws Exception {
        return mockMvc.perform(get(url).with(user(new StudentPrincipal(student))));
    }

    @Test
    @DisplayName("TC-002-01 Logged-in student opens the registration form")
    public void formShowsTheSignedInStudentAsTheFoundingOfficer() throws Exception {
        Student student = saveStudent(butch);

        open("/clubForm", student)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Founding officer")))
                .andExpect(content().string(containsString(butch.firstName() + " " + butch.lastName())))
                .andExpect(content().string(containsString(butch.email())))
                .andExpect(content().string(not(containsString("name=\"founderStudentNumber\""))));
    }

    @Test
    @DisplayName("TC-002-02 Unauthenticated user requests the form")
    public void signedOutVisitorOpeningTheFormIsSentToSignIn() throws Exception {
        mockMvc.perform(get("/clubForm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("TC-002-03 Unauthenticated submission")
    public void signedOutSubmissionIsNotSaved() throws Exception {
        long clubsBefore = clubRepository.count();

        mockMvc.perform(validRegistration("Signed Out Club"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    @Test
    @DisplayName("TC-002-04 Submitter can view their pending club")
    public void submitterSeesTheirPendingClub() throws Exception {
        Student student = saveStudent(butch);
        String clubPage = register(student, "Robotics Club").andReturn().getResponse().getRedirectedUrl();

        open(clubPage, student)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Robotics Club")))
                .andExpect(content().string(containsString(">Pending<")));
    }

    @Test
    @DisplayName("TC-002-05 The founder is the signed-in student")
    public void founderIsTheSignedInStudentNotTheWsuIdSent() throws Exception {
        Student signedIn = saveStudent(butch);
        Student other = saveStudent(alex);

        mockMvc.perform(validRegistration("Founder Check Club")
                        .with(user(new StudentPrincipal(signedIn)))
                        .param("founderStudentNumber", other.getStudentNumber()))
                .andExpect(status().is3xxRedirection());

        List<Club> clubs = clubRepository.findByFounderIdOrderByClubIDDesc(signedIn.getId());
        assertThat(clubs).hasSize(1);
        Club club = clubs.get(0);
        assertThat(club.getStatus()).isEqualTo(ClubStatus.PENDING);
        assertThat(club.getFounder().getId()).isEqualTo(signedIn.getId());
        assertThat(clubRepository.findByFounderIdOrderByClubIDDesc(other.getId())).isEmpty();

        List<Member> members = club.getMembers();
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getStudent().getId()).isEqualTo(signedIn.getId());
        assertThat(members.get(0).getRole()).isEqualTo(ClubRole.PRESIDENT);
    }

    @Test
    @DisplayName("TC-002-06 Registration is confirmed")
    public void registrationIsConfirmedOnceOnTheNewClubsPage() throws Exception {
        Student student = saveStudent(butch);
        MvcResult registered = register(student, "Chess Club")
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clubs/*"))
                .andReturn();
        String clubPage = registered.getResponse().getRedirectedUrl();

        mockMvc.perform(get(clubPage).with(user(new StudentPrincipal(student))).flashAttrs(registered.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CONFIRMATION)))
                .andExpect(content().string(containsString(MY_CLUB_REQUESTS_LINK)));

        open(clubPage, student)
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(CONFIRMATION))));
        assertThat(clubRepository.findByFounderIdOrderByClubIDDesc(student.getId())).hasSize(1);
    }

    @Test
    @DisplayName("TC-002-07 My Club Requests lists only the student's own clubs")
    public void myClubRequestsListsOnlyTheStudentsOwnClubsNewestFirst() throws Exception {
        Student student = saveStudent(butch);
        Student other = saveStudent(alex);
        register(student, "Older Club").andExpect(status().is3xxRedirection());
        register(student, "Newer Club").andExpect(status().is3xxRedirection());
        register(other, "Alex's Club").andExpect(status().is3xxRedirection());

        String page = open("/clubs/mine", student)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(page).contains("Older Club", "Newer Club", ">Pending<");
        assertThat(page).doesNotContain("Alex");
        assertThat(page.indexOf("Newer Club")).isLessThan(page.indexOf("Older Club"));
        for (Club club : clubRepository.findByFounderIdOrderByClubIDDesc(student.getId())) {
            assertThat(page).contains("href=\"/clubs/" + club.getClubID() + "\"");
        }
    }

    @Test
    @DisplayName("TC-002-08 Confirmation after submitting")
    public void successMessageAndLinkAfterSubmitting() throws Exception {
        Student student = saveStudent(butch);
        open("/clubForm", student).andExpect(status().isOk());

        MvcResult registered = register(student, "Hiking Club")
                .andExpect(flash().attribute("registered", true))
                .andReturn();

        mockMvc.perform(get(registered.getResponse().getRedirectedUrl())
                        .with(user(new StudentPrincipal(student)))
                        .flashAttrs(registered.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CONFIRMATION)))
                .andExpect(content().string(containsString(MY_CLUB_REQUESTS_LINK)));
    }

    @Test
    @DisplayName("TC-002-09 No requests yet")
    public void myClubRequestsSaysThereAreNoneYet() throws Exception {
        Student student = saveStudent(butch);

        String page = open("/clubs/mine", student)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String content = page.substring(page.indexOf("<h1>"));
        assertThat(content).contains("No club requests yet", "href=\"/clubForm\"");
    }
}
