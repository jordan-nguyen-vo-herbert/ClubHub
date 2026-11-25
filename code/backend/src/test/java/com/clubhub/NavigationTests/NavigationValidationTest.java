package com.clubhub.NavigationTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import jakarta.servlet.RequestDispatcher;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Specification-based tests for FR-006.1 Front End: Navigation and error pages (#56), one test per test case in
 * specs/features/navigation/validation.md (TEST-012, #61).
 *
 * <p>
 * MockMvc doesn't forward an error to /error the way the servlet container does, so a 404 from a real URL comes
 * back with an empty body. Each error test checks the status on the real URL, then renders the page Spring Boot
 * would show by calling /error with the same status, the way the container does.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class NavigationValidationTest {

    private static final String NOT_FOUND_MESSAGE = "This page could not be loaded";
    private static final String HOME_LINK = "href=\"/\"";
    private static final long MISSING_ID = 999_999_999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubRepository clubRepository;

    private final StudentData butch = SampleStudents.ALL.get(0);

    /**
     * Saves Butch straight to the database, the way a created profile is stored.
     *
     * @return the saved student, with its database ID
     */
    private Student saveButch() {
        return studentRepository.save(new Student(butch.studentNumber(), butch.firstName(), butch.lastName(),
                butch.email(), butch.major(), null, null, butch.password()));
    }

    /**
     * Saves a club founded by this student, so the club page has something to show.
     *
     * @param founder the club's founder
     * @return the saved club, with its ID
     */
    private Club saveClub(Student founder) {
        return clubRepository.save(new Club("Navigation Test Club", AreaOfInterest.OTHER, "A club for navigation tests",
                founder));
    }

    /**
     * Builds a GET request signed in as this student.
     *
     * @param url     the page to open
     * @param student the signed-in student
     * @return the request, ready to perform
     */
    private MockHttpServletRequestBuilder getAs(String url, Student student) {
        return get(url).with(user(new StudentPrincipal(student)));
    }

    /**
     * Opens a page as this student and returns its HTML, checking it loaded with HTTP 200.
     *
     * @param url     the page to open
     * @param student the signed-in student, or null for a signed-out visitor
     * @return the page's HTML
     * @throws Exception if the request fails
     */
    private String page(String url, Student student) throws Exception {
        MockHttpServletRequestBuilder request = student == null ? get(url) : getAs(url, student);
        return mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Renders the error page Spring Boot shows for this status, the way the servlet container does after an error.
     *
     * @param status  the HTTP status of the error
     * @param uri     the URL that failed
     * @param student the signed-in student
     * @return the result, ready for expectations
     * @throws Exception if the request fails
     */
    private ResultActions errorPage(int status, String uri, Student student) throws Exception {
        return mockMvc.perform(getAs("/error", student)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, status)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, uri)
                .accept(MediaType.TEXT_HTML));
    }

    /**
     * Returns the shared header of a page.
     *
     * @param page the page's HTML
     * @return everything from the opening header tag to the closing one
     */
    private String header(String page) {
        int start = page.indexOf("<header");
        assertThat(start).as("the page has the shared header").isNotNegative();
        return page.substring(start, page.indexOf("</header>", start) + "</header>".length());
    }

    /**
     * Returns the page's main content, the section after the header.
     *
     * @param page the page's HTML
     * @return everything from the opening section tag to the closing one
     */
    private String section(String page) {
        int start = page.indexOf("<section");
        return page.substring(start, page.indexOf("</section>", start));
    }

    /**
     * Lists the links in some HTML that stay inside ClubHub, in the order they appear.
     *
     * @param html the HTML to search
     * @return each local link once
     */
    private Set<String> localLinks(String html) {
        Set<String> links = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("<a[^>]*href=\"(/[^\"]*)\"").matcher(html);
        while (matcher.find()) {
            links.add(matcher.group(1));
        }
        return links;
    }

    /**
     * Returns the text of the page's title.
     *
     * @param page the page's HTML
     * @return the title, trimmed
     */
    private String title(String page) {
        Matcher matcher = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL).matcher(page);
        assertThat(matcher.find()).as("the page has a title").isTrue();
        return matcher.group(1).trim();
    }

    /**
     * Opens a link the way a browser does, following one redirect, and checks the page it ends on loads with
     * HTTP 200 and follows the title pattern (FR-006-07).
     *
     * @param link    the link to open
     * @param student the signed-in student
     * @throws Exception if a request fails
     */
    private void linkOpensItsPage(String link, Student student) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(getAs(link, student)).andReturn().getResponse();
        String landedOn = link;
        if (response.getStatus() / 100 == 3) {
            landedOn = response.getRedirectedUrl();
            response = mockMvc.perform(getAs(landedOn, student)).andReturn().getResponse();
        }

        assertThat(response.getStatus()).as("status of " + link).isEqualTo(200);
        String pageTitle = title(response.getContentAsString());
        if (landedOn.equals("/")) {
            assertThat(pageTitle).isEqualTo("ClubHub");
        } else {
            assertThat(pageTitle).as("title of " + landedOn).matches(".+ - ClubHub");
        }
    }

    /**
     * Checks that a page shows no technical details: no stack trace, exception, Java class name or SQL.
     *
     * @param page the page's HTML
     */
    private void hidesTechnicalDetails(String page) {
        assertThat(page).doesNotContain("Exception", "java.", "com.clubhub", "\tat ", "SELECT ", "select ",
                "Whitelabel");
    }

    @Test
    @DisplayName("TC-006-01 Navigate to an available subdirectory")
    public void everyLinkOnTheHomePageAndHeaderOpensItsPage() throws Exception {
        Student student = saveButch();
        String home = page("/", student);

        Set<String> links = new LinkedHashSet<>(localLinks(section(home)));
        links.addAll(localLinks(header(home)));
        assertThat(links).contains("/", "/clubs/search", "/clubForm", "/profile");

        for (String link : links) {
            linkOpensItsPage(link, student);
        }
    }

    @Test
    @DisplayName("TC-006-02 Navigate to an unavailable directory")
    public void linkToARemovedPageShowsThePageNotFound() throws Exception {
        Student student = saveButch();

        mockMvc.perform(getAs("/old-page", student)).andExpect(status().isNotFound());

        String page = errorPage(404, "/old-page", student)
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        assertThat(section(page)).contains(NOT_FOUND_MESSAGE, HOME_LINK, "Back to home");
        page("/", student);
    }

    @Test
    @DisplayName("TC-006-03 Navigation header appears on every page")
    public void everyPageShowsTheSameNavigationHeader() throws Exception {
        Student student = saveButch();
        Club club = saveClub(student);

        List<String> pages = List.of(
                page("/", student),
                page("/clubs/search", student),
                page("/clubForm", student),
                page("/clubs/" + club.getClubID(), student),
                page("/students/" + student.getId(), student),
                errorPage(404, "/clubs/abc/xyz", student).andReturn().getResponse().getContentAsString(),
                errorPage(400, "/clubs/search/filter", student).andReturn().getResponse().getContentAsString(),
                page("/studentForm", null),
                page("/login", null));

        for (String page : pages) {
            assertThat(header(page)).contains(">Home<", HOME_LINK, "href=\"/clubs/search\"", "href=\"/clubForm\"");
        }
    }

    @Test
    @DisplayName("TC-006-04 Return home from any page")
    public void homeInTheHeaderOpensTheHomePage() throws Exception {
        Student student = saveButch();
        Club club = saveClub(student);

        for (String url : List.of("/clubs/search", "/clubForm", "/clubs/" + club.getClubID())) {
            assertThat(header(page(url, student))).containsPattern("<a[^>]*href=\"/\"[^>]*>Home</a>");
        }
        assertThat(title(page("/", student))).isEqualTo("ClubHub");
    }

    @Test
    @DisplayName("TC-006-05 Header links follow the sign-in state")
    public void headerLinksFollowTheSignInState() throws Exception {
        Student student = saveButch();

        for (String url : List.of("/", "/studentForm")) {
            String signedOut = header(page(url, null));
            assertThat(signedOut).contains("Create a student profile", "href=\"/login\"");
            assertThat(signedOut).doesNotContain("My student profile", "Sign out");
        }

        for (String url : List.of("/", "/clubs/search")) {
            String signedIn = header(page(url, student));
            assertThat(signedIn).contains("My student profile", butch.firstName(), "Sign out");
            assertThat(signedIn).doesNotContain("Create a student profile", "href=\"/login\"");
        }
    }

    @Test
    @DisplayName("TC-006-06 Page does not exist")
    public void urlThatDoesNotExistShowsThePageNotFound() throws Exception {
        Student student = saveButch();

        mockMvc.perform(getAs("/clubs/abc/xyz", student)).andExpect(status().isNotFound());

        String page = errorPage(404, "/clubs/abc/xyz", student)
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        assertThat(section(page)).contains(NOT_FOUND_MESSAGE, HOME_LINK);
    }

    @Test
    @DisplayName("TC-006-07 Club or student does not exist")
    public void missingClubOrStudentShowsThePageNotFound() throws Exception {
        Student student = saveButch();
        assertThat(clubRepository.existsById(MISSING_ID)).isFalse();
        assertThat(studentRepository.existsById(MISSING_ID)).isFalse();

        for (String url : List.of("/clubs/" + MISSING_ID, "/students/" + MISSING_ID)) {
            mockMvc.perform(getAs(url, student)).andExpect(status().isNotFound());

            String page = errorPage(404, url, student)
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
            assertThat(section(page)).contains(NOT_FOUND_MESSAGE, HOME_LINK);
        }
    }

    @Test
    @DisplayName("TC-006-08 Bad request shows the general error page")
    public void badRequestShowsTheGeneralErrorPage() throws Exception {
        Student student = saveButch();

        mockMvc.perform(getAs("/clubs/search/filter?area=NOT_AN_AREA", student)).andExpect(status().isBadRequest());

        String page = errorPage(400, "/clubs/search/filter", student)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertThat(section(page)).contains("400", HOME_LINK, "Back to home");
        assertThat(page).doesNotContain("Whitelabel Error Page");
    }

    @Test
    @DisplayName("TC-006-09 Error pages hide technical details")
    public void errorPagesHideTechnicalDetails() throws Exception {
        Student student = saveButch();

        hidesTechnicalDetails(errorPage(404, "/clubs/abc/xyz", student)
                .andReturn().getResponse().getContentAsString());
        hidesTechnicalDetails(errorPage(400, "/clubs/search/area", student)
                .andReturn().getResponse().getContentAsString());

        String serverError = mockMvc.perform(getAs("/error", student)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/clubs/search")
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION,
                                new IllegalStateException("SELECT password FROM student"))
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "SELECT password FROM student")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();
        hidesTechnicalDetails(serverError);
        assertThat(serverError).doesNotContain("password FROM");
    }

    @Test
    @DisplayName("A signed-out visitor is sent to sign in before any error page (FR-010-05)")
    public void signedOutVisitorIsSentToSignInBeforeAnyErrorPage() throws Exception {
        for (String url : List.of("/clubs/abc/xyz", "/clubs/" + MISSING_ID)) {
            mockMvc.perform(get(url))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }
}
