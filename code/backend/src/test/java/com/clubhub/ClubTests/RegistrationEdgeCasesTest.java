package com.clubhub.ClubTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubStatus;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
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
 * Tests for FR-002.6 Back End: Registration edge cases (#52), one test per scenario on the issue plus the
 * server-side checks behind them. They count toward TEST-012 (#61).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegistrationEdgeCasesTest {

    private static final String DUPLICATE_NAME = "A club with this name already exists";
    private static final String AREA_ERROR = "Choose an area of interest from the list";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubRepository clubRepository;

    private final StudentData butch = SampleStudents.ALL.get(0);

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
     * The answers of a complete, valid registration. Each test changes the fields it is about.
     *
     * @return the form fields and their values, in form order
     */
    private Map<String, String> validRegistration() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("clubName", "Chess Club");
        fields.put("areaOfInterest", AreaOfInterest.GAMING.name());
        fields.put("description", "Casual and competitive chess every week");
        fields.put("meetingInfo", "Thursdays 7 PM, CUE 203");
        fields.put("facultyAdvisor", "Dr. Rook");
        fields.put("contactEmail", "chess.club@wsu.edu");
        return fields;
    }

    /**
     * Submits the registration form as a signed-in student, the way the browser does.
     *
     * @param founder the signed-in student
     * @param fields  the form fields to send
     * @return the result, ready for expectations
     * @throws Exception if the request fails
     */
    private ResultActions register(Student founder, Map<String, String> fields) throws Exception {
        MockHttpServletRequestBuilder request = post("/clubForm").with(csrf()).with(user(new StudentPrincipal(founder)));
        fields.forEach(request::param);
        return mockMvc.perform(request);
    }

    /**
     * Finds the opening tag of the input, select or textarea with this name in a page.
     *
     * @param page the page's HTML
     * @param name the field's name attribute
     * @return the whole opening tag
     */
    private String fieldTag(String page, String name) {
        Matcher matcher = Pattern.compile("<(input|select|textarea)[^>]*name=\"" + name + "\"[^>]*>").matcher(page);
        assertThat(matcher.find()).as("the page has a field named " + name).isTrue();
        return matcher.group();
    }

    /**
     * Finds the label of a field, including the required mark inside it.
     *
     * @param page the page's HTML
     * @param id   the field's id
     * @return the whole label element
     */
    private String label(String page, String id) {
        Matcher matcher = Pattern.compile("<label for=\"" + id + "\">.*?</label>", Pattern.DOTALL).matcher(page);
        assertThat(matcher.find()).as("the page has a label for " + id).isTrue();
        return matcher.group();
    }

    /**
     * Scenario: Form displays all fields. The six fields are there, and only the required ones are marked
     * and required by the browser.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void formDisplaysAllFieldsAndMarksTheRequiredOnes() throws Exception {
        Student student = saveStudent(butch);

        String page = mockMvc.perform(get("/clubForm").with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        for (String required : List.of("clubName", "areaOfInterest", "description", "contactEmail")) {
            assertThat(label(page, required)).contains("required-mark");
            assertThat(fieldTag(page, required)).contains("required");
        }
        for (String optional : List.of("meetingInfo", "facultyAdvisor")) {
            assertThat(label(page, optional)).doesNotContain("required-mark");
            assertThat(fieldTag(page, optional)).doesNotContain("required");
        }
        assertThat(page).contains("Club name", "Area of interest", "Description", "Meeting info",
                "Faculty advisor", "Contact email", "are required");
    }

    /**
     * Scenario: Duplicate club name. The check ignores capitalization, and the message is shown next to the
     * club name, before the next field.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void duplicateClubNameIsNotSaved() throws Exception {
        Student student = saveStudent(butch);
        clubRepository.save(new Club("Chess Club", AreaOfInterest.GAMING, "The first chess club", student));
        long clubsBefore = clubRepository.count();

        Map<String, String> fields = validRegistration();
        fields.put("clubName", "chess club");
        String page = register(student, fields)
                .andExpect(status().isOk())
                .andExpect(view().name("clubForm"))
                .andExpect(model().attributeHasFieldErrors("clubForm", "clubName"))
                .andReturn().getResponse().getContentAsString();

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
        int nameField = page.indexOf("name=\"clubName\"");
        int message = page.indexOf(DUPLICATE_NAME);
        assertThat(message).isGreaterThan(nameField);
        assertThat(message).isLessThan(page.indexOf("name=\"areaOfInterest\""));
    }

    /**
     * Scenario: Invalid category. An area that isn't in the list is refused with a readable message next to
     * the area, never Spring's conversion message with a Java class name.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void areaOfInterestNotInTheListIsNotSaved() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        Map<String, String> fields = validRegistration();
        fields.put("areaOfInterest", "NOT_AN_AREA");
        String page = register(student, fields)
                .andExpect(status().isOk())
                .andExpect(view().name("clubForm"))
                .andExpect(model().attributeHasFieldErrors("clubForm", "areaOfInterest"))
                .andExpect(content().string(not(containsString("com.clubhub"))))
                .andExpect(content().string(not(containsString("Failed to convert"))))
                .andReturn().getResponse().getContentAsString();

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
        int message = page.indexOf(AREA_ERROR);
        assertThat(message).isGreaterThan(page.indexOf("name=\"areaOfInterest\""));
        assertThat(message).isLessThan(page.indexOf("name=\"description\""));
    }

    /**
     * A request with no area at all, which the browser's required check would normally stop, gets the same
     * message.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void missingAreaOfInterestIsNotSaved() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        Map<String, String> fields = validRegistration();
        fields.put("areaOfInterest", "");
        register(student, fields)
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("clubForm", "areaOfInterest"))
                .andExpect(content().string(containsString(AREA_ERROR)));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }

    /**
     * Scenario: Valid submission is saved with its timestamp. The club is Pending, keeps every field, and its
     * submission time falls between just before and just after the request.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void validSubmissionIsSavedPendingWithItsTimestamp() throws Exception {
        Student student = saveStudent(butch);

        Instant before = Instant.now();
        register(student, validRegistration())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clubs/*"));
        Instant after = Instant.now();

        List<Club> clubs = clubRepository.findByFounderIdOrderByClubIDDesc(student.getId());
        assertThat(clubs).hasSize(1);
        Club club = clubs.get(0);
        assertThat(club.getStatus()).isEqualTo(ClubStatus.PENDING);
        assertThat(club.getSubmittedAt()).isNotNull();
        assertThat(club.getSubmittedAt()).isBetween(before.minusSeconds(1), after.plusSeconds(1));
        assertThat(club.getMeetingInfo()).isEqualTo("Thursdays 7 PM, CUE 203");
        assertThat(club.getFacultyAdvisor()).isEqualTo("Dr. Rook");
        assertThat(club.getContactEmail()).isEqualTo("chess.club@wsu.edu");
    }

    /**
     * The club page shows the new fields, and "Not provided" for an optional field left blank.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void clubPageShowsTheNewFields() throws Exception {
        Student student = saveStudent(butch);

        Map<String, String> fields = validRegistration();
        fields.put("facultyAdvisor", "");
        String clubPage = register(student, fields).andReturn().getResponse().getRedirectedUrl();

        mockMvc.perform(get(clubPage).with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Thursdays 7 PM, CUE 203")))
                .andExpect(content().string(containsString("Not provided")))
                .andExpect(content().string(containsString("mailto:chess.club@wsu.edu")));

        Club club = clubRepository.findByFounderIdOrderByClubIDDesc(student.getId()).get(0);
        assertThat(club.getFacultyAdvisor()).isNull();
    }

    /**
     * Scenario: Server errors are shown inline. The browser accepts an address the server rejects, the error
     * appears next to that field, and every value the student typed stays in the form.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void serverErrorsAreShownInlineAndTheTypedValuesStay() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        Map<String, String> fields = validRegistration();
        fields.put("contactEmail", "chess.club@wsu");
        String page = register(student, fields)
                .andExpect(status().isOk())
                .andExpect(view().name("clubForm"))
                .andExpect(model().attributeHasFieldErrors("clubForm", "contactEmail"))
                .andReturn().getResponse().getContentAsString();

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
        assertThat(page.indexOf("Enter a valid email")).isGreaterThan(page.indexOf("name=\"contactEmail\""));
        assertThat(fieldTag(page, "clubName")).contains("value=\"Chess Club\"");
        assertThat(fieldTag(page, "meetingInfo")).contains("value=\"Thursdays 7 PM, CUE 203\"");
        assertThat(fieldTag(page, "facultyAdvisor")).contains("value=\"Dr. Rook\"");
        assertThat(fieldTag(page, "contactEmail")).contains("value=\"chess.club@wsu\"");
        assertThat(page).contains("Casual and competitive chess every week");
    }

    /**
     * A request that skips the browser's checks and leaves every required text field blank gets an error on
     * each of them, and nothing is saved.
     *
     * @throws Exception if a request or check fails
     */
    @Test
    public void blankRequiredFieldsAreRefusedOnTheServer() throws Exception {
        Student student = saveStudent(butch);
        long clubsBefore = clubRepository.count();

        Map<String, String> fields = validRegistration();
        fields.put("clubName", "   ");
        fields.put("description", "");
        fields.put("contactEmail", "");
        register(student, fields)
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("clubForm", "clubName", "description", "contactEmail"))
                .andExpect(content().string(containsString("Enter a club name")))
                .andExpect(content().string(containsString("Enter a description")))
                .andExpect(content().string(containsString("Enter a contact email")));

        assertThat(clubRepository.count()).isEqualTo(clubsBefore);
    }
}
