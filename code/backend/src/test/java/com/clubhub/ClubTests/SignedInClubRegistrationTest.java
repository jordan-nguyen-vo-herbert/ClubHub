package com.clubhub.ClubTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;
import com.clubhub.support.WithMockStudent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
 * White-box tests for signed-in club registration, FR-002.7 (#50). Each test checks one scenario from
 * specs/features/club-registration/requirements.md. "The founder is the signed-in student" is in ClubRegistrationTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignedInClubRegistrationTest {

    private static final String CONFIRMATION = "was submitted and is waiting for approval";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubRepository clubRepository;

    private final StudentData butch = SampleStudents.ALL.get(0);
    private final StudentData alex = SampleStudents.ALL.get(2);

    // Saves a student straight to the database, the way a created profile is stored
    private Student saveStudent(StudentData student) {
        return studentRepository.save(new Student(student.studentNumber(), student.firstName(), student.lastName(),
                student.email(), student.major(), null, null, student.password()));
    }

    // Submits the registration form as this student, the way the browser does once they're signed in
    private ResultActions register(Student student, String clubName) throws Exception {
        return mockMvc.perform(post("/clubForm").with(csrf()).with(user(new StudentPrincipal(student)))
                .param("clubName", clubName)
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "A club for testing")
                .param("contactEmail", "club.contact@wsu.edu"));
    }

    /**
     * Scenario: Logged-in student opens the registration form.
     */
    @Test
    public void formShowsTheSignedInStudentAsTheFoundingOfficer() throws Exception {
        Student student = saveStudent(butch);

        mockMvc.perform(get("/clubForm").with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(butch.firstName() + " " + butch.lastName())))
                .andExpect(content().string(containsString(butch.email())))
                .andExpect(content().string(not(containsString("founderStudentNumber"))));
    }

    /**
     * Scenario: Unauthenticated user requests the form.
     */
    @Test
    public void signedOutVisitorOpeningTheFormIsSentToSignIn() throws Exception {
        mockMvc.perform(get("/clubForm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Scenario: Unauthenticated submission, when the CSRF token is valid.
     */
    @Test
    public void signedOutSubmissionIsNotSavedAndGoesToSignIn() throws Exception {
        long clubsBefore = clubRepository.count();

        mockMvc.perform(post("/clubForm").with(csrf())
                        .param("clubName", "Signed Out Club")
                        .param("areaOfInterest", AreaOfInterest.values()[0].name())
                        .param("description", "This should never be saved"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    /**
     * Scenario: Unauthenticated submission, the way a real browser reaches it: a form left open after signing out
     * or a session timeout has a stale CSRF token, and the CSRF check runs first.
     */
    @Test
    public void staleFormFromASignedOutVisitorGoesToSignIn() throws Exception {
        long clubsBefore = clubRepository.count();

        mockMvc.perform(post("/clubForm").with(csrf().useInvalidToken())
                        .param("clubName", "Stale Form Club")
                        .param("areaOfInterest", AreaOfInterest.values()[0].name())
                        .param("description", "This should never be saved"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    /**
     * The redirect is only for that case. A bad CSRF token on a public form, or from a signed-in student,
     * still gets the 403 from NFR-010-03.
     */
    @Test
    public void badCsrfTokenIsStillRefusedEverywhereElse() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        mockMvc.perform(post("/studentForm").with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/clubForm").with(csrf().useInvalidToken()).with(user(new StudentPrincipal(student)))
                        .param("clubName", "Bad Token Club")
                        .param("areaOfInterest", AreaOfInterest.values()[0].name())
                        .param("description", "This should never be saved"))
                .andExpect(status().isForbidden());

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    /**
     * Scenario: Submitter can view their pending club.
     */
    @Test
    public void founderSeesTheirPendingClub() throws Exception {
        Student student = saveStudent(butch);
        String clubPage = register(student, "Robotics Club").andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(get(clubPage).with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Robotics Club")))
                .andExpect(content().string(containsString(">Pending<")));
    }

    /**
     * Scenario: Registration is confirmed. The message shows once, and a refresh doesn't register the club again.
     */
    @Test
    public void registrationIsConfirmedOnceOnTheClubPage() throws Exception {
        Student student = saveStudent(butch);
        MvcResult registered = register(student, "Chess Club")
                .andExpect(redirectedUrlPattern("/clubs/*"))
                .andExpect(flash().attribute("registered", true))
                .andReturn();
        String clubPage = registered.getResponse().getRedirectedUrl();

        // The page right after registering, carrying the flash attribute the way the browser's redirect does
        mockMvc.perform(get(clubPage).with(user(new StudentPrincipal(student))).flashAttrs(registered.getFlashMap()))
                .andExpect(content().string(containsString(CONFIRMATION)))
                .andExpect(content().string(containsString("href=\"/clubs/mine\"")));

        // Refreshing: the flash attribute is gone, so the club shows without the message
        mockMvc.perform(get(clubPage).with(user(new StudentPrincipal(student))))
                .andExpect(content().string(not(containsString(CONFIRMATION))));
        assertThat(clubRepository.findByFounderIdOrderByClubIDDesc(student.getId())).hasSize(1);
    }

    /**
     * Scenario: My Club Requests lists only the student's own clubs.
     */
    @Test
    public void myClubRequestsListsOnlyTheStudentsOwnClubsNewestFirst() throws Exception {
        Student student = saveStudent(butch);
        register(student, "Older Club");
        register(student, "Newer Club");
        register(saveStudent(alex), "Another Students Club");

        String page = mockMvc.perform(get("/clubs/mine").with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andExpect(view().name("myClubs"))
                .andExpect(content().string(containsString(">Pending<")))
                .andExpect(content().string(not(containsString("Another Students Club"))))
                .andReturn().getResponse().getContentAsString();

        assertThat(page.indexOf("Older Club")).isGreaterThan(page.indexOf("Newer Club"));
        assertThat(page.indexOf("Newer Club")).isNotNegative();
    }

    /**
     * Scenario: No requests yet.
     */
    @Test
    public void myClubRequestsSaysSoWhenThereAreNone() throws Exception {
        Student student = saveStudent(butch);

        String page = mockMvc.perform(get("/clubs/mine").with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Skips the shared header, which links to the form too
        assertThat(page.substring(page.indexOf("<h1>")))
                .contains("No club requests yet")
                .contains("href=\"/clubForm\"");
    }

    /**
     * My Club Requests needs sign-in like every other club page (FR-010-05).
     */
    @Test
    public void signedOutVisitorOpeningMyClubRequestsIsSentToSignIn() throws Exception {
        mockMvc.perform(get("/clubs/mine"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * A form with an error comes back with the founding officer still filled in (FR-002-02).
     */
    @Test
    public void formWithAnErrorComesBackWithTheFounder() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        mockMvc.perform(post("/clubForm").with(csrf()).with(user(new StudentPrincipal(student)))
                        .param("clubName", "Bad Area Club")
                        .param("areaOfInterest", "NOT_AN_AREA")
                        .param("description", "Testing a form error"))
                .andExpect(status().isOk())
                .andExpect(view().name("clubForm"))
                .andExpect(model().attributeHasFieldErrors("clubForm", "areaOfInterest"))
                .andExpect(content().string(containsString(butch.email())));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    /**
     * A signed-in user with no profile behind them is refused, instead of saving a club without a founder.
     */
    @Test
    @WithMockStudent // made up and never saved, so it has no database ID
    public void signedInUserWithNoProfileIsRefused() throws Exception {
        long clubsBefore = clubRepository.count();

        mockMvc.perform(get("/clubForm")).andExpect(status().isForbidden());
        mockMvc.perform(get("/clubs/mine")).andExpect(status().isForbidden());
        mockMvc.perform(post("/clubForm").with(csrf())
                        .param("clubName", "No Founder Club")
                        .param("areaOfInterest", AreaOfInterest.values()[0].name())
                        .param("description", "This should never be saved"))
                .andExpect(status().isForbidden());

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }
}
