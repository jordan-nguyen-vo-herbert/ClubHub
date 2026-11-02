package com.clubhub.SignInTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.PasswordMigration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.clubhub.StudentTests.SampleStudents.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Specification-based tests for sign-in, FR-010.1 (#49) and FR-010.2 (#55), one test per test case in
 * specs/features/sign-in/validation.md (TEST-011, #60). Each test goes through the real sign-in form and keeps the
 * browser's session between requests, the way a student would, instead of signing in a test user directly.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignInValidationTest {

    private static final String SIGN_IN_ERROR = "A valid school account is required";
    private static final Pattern BCRYPT_HASH = Pattern.compile("\\$2[aby]\\$\\d{2}\\$.{53}");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordMigration passwordMigration;

    private final StudentData butch = SampleStudents.ALL.get(0);
    private final StudentData priya = SampleStudents.ALL.get(1);
    private final StudentData alex = SampleStudents.ALL.get(2);

    /**
     * Saves a student with a hashed password, the way a profile created through the form is stored.
     *
     * @param student the sample student to save
     * @return the saved student, with its database ID
     */
    private Student saveStudent(StudentData student) {
        return studentRepository.save(new Student(student.studentNumber(), student.firstName(), student.lastName(),
                student.email(), student.major(), null, null, passwordEncoder.encode(student.password())));
    }

    /**
     * Copies a sample student with another WSU ID and email, to test the email rules.
     *
     * @param student       the sample student to copy
     * @param studentNumber the new WSU ID
     * @param email         the new email
     * @return the copy
     */
    private StudentData withEmail(StudentData student, String studentNumber, String email) {
        return new StudentData(studentNumber, student.firstName(), student.lastName(), email,
                student.major(), student.gender(), student.pronouns(), student.password());
    }

    /**
     * Sends the sign-in form, continuing an existing browser session when one is given.
     *
     * @param email    the typed email
     * @param password the typed password
     * @param session  the browser's session, or null for a new one
     * @return the result, ready for expectations
     * @throws Exception if the request fails
     */
    private ResultActions signIn(String email, String password, MockHttpSession session) throws Exception {
        var request = post("/login").with(csrf()).param("email", email).param("password", password);
        return mockMvc.perform(session == null ? request : request.session(session));
    }

    /**
     * Signs a student in through the form and returns the browser's session.
     *
     * @param student the sample student to sign in
     * @return the signed-in session
     * @throws Exception if the request fails
     */
    private MockHttpSession signedInSession(StudentData student) throws Exception {
        MvcResult result = signIn(student.email(), student.password(), null)
                .andExpect(authenticated())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    /**
     * Returns the session the server started for a request.
     *
     * @param result a finished request
     * @return the request's session
     */
    private MockHttpSession sessionOf(MvcResult result) {
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    /**
     * Opens a page in this session and returns its HTML, checking it loaded with HTTP 200.
     *
     * @param url     the page to open
     * @param session the browser's session, or null for a signed-out visitor
     * @return the page's HTML
     * @throws Exception if the request fails
     */
    private String page(String url, MockHttpSession session) throws Exception {
        var request = get(url);
        return mockMvc.perform(session == null ? request : request.session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Returns the shared header of a page.
     *
     * @param page the page's HTML
     * @return everything from the opening header tag to the closing one
     */
    private String header(String page) {
        int start = page.indexOf("<header");
        return page.substring(start, page.indexOf("</header>", start));
    }

    /**
     * Finds the opening tag of the input with this name.
     *
     * @param page the page's HTML
     * @param name the input's name attribute
     * @return the whole opening tag
     */
    private String inputTag(String page, String name) {
        Matcher matcher = Pattern.compile("<input[^>]*name=\"" + name + "\"[^>]*>").matcher(page);
        assertThat(matcher.find()).as("the page has an input named " + name).isTrue();
        return matcher.group();
    }

    /**
     * Counts how many times some text appears in a page.
     *
     * @param page the page's HTML
     * @param text the text to count
     * @return the number of times it appears
     */
    private int occurrences(String page, String text) {
        return page.split(Pattern.quote(text), -1).length - 1;
    }

    @Test
    @DisplayName("TC-010-01 Successful sign-in")
    public void successfulSignInGoesToTheHomePage() throws Exception {
        saveStudent(butch);

        signIn(butch.email(), butch.password(), null)
                .andExpect(authenticated().withUsername(butch.email()))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("TC-010-02 Sign-in with invalid credentials")
    public void wrongPasswordAndUnknownEmailGetTheSameError() throws Exception {
        saveStudent(butch);

        MvcResult wrongPassword = signIn(butch.email(), "not-the-password", null)
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"))
                .andReturn();
        MvcResult unknownEmail = signIn("nobody@wsu.edu", "anything", null)
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"))
                .andReturn();

        for (MvcResult failed : List.of(wrongPassword, unknownEmail)) {
            assertThat(page("/login?error", sessionOf(failed))).contains(SIGN_IN_ERROR);
        }
    }

    @Test
    @DisplayName("TC-010-03 Signed-out student opens a page that needs sign-in")
    public void signedOutVisitorIsSentToSignInOnlyForProtectedPages() throws Exception {
        for (String url : List.of("/clubs/search", "/clubForm")) {
            mockMvc.perform(get(url))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
        for (String url : List.of("/", "/login", "/studentForm")) {
            page(url, null);
        }
    }

    @Test
    @DisplayName("TC-010-04 Sign-in returns to the page the student wanted")
    public void signInReturnsToThePageTheStudentWanted() throws Exception {
        saveStudent(butch);

        MvcResult attempt = mockMvc.perform(get("/clubs/search"))
                .andExpect(redirectedUrlPattern("**/login"))
                .andReturn();

        String target = signIn(butch.email(), butch.password(), sessionOf(attempt))
                .andExpect(authenticated())
                .andReturn().getResponse().getRedirectedUrl();
        assertThat(target).contains("/clubs/search");
    }

    @Test
    @DisplayName("TC-010-05 Passwords are stored hashed")
    public void passwordIsStoredAsABcryptHash() throws Exception {
        MvcResult created = mockMvc.perform(submit(butch))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String stored = studentRepository.findByEmail(butch.email()).orElseThrow().getPassword();
        assertThat(stored).isNotEqualTo(butch.password());
        assertThat(BCRYPT_HASH.matcher(stored).matches()).isTrue();
        assertThat(passwordEncoder.matches(butch.password(), stored)).isTrue();

        String profile = page(created.getResponse().getRedirectedUrl(), sessionOf(created));
        assertThat(profile).doesNotContain(butch.password(), stored);
    }

    @Test
    @DisplayName("TC-010-06 Existing plain-text passwords are hashed at startup")
    public void plainTextPasswordIsHashedAtStartupAndStillWorks() throws Exception {
        Student legacy = studentRepository.save(new Student(priya.studentNumber(), priya.firstName(), priya.lastName(),
                priya.email(), priya.major(), null, null, priya.password()));

        passwordMigration.run(null);
        String hash = studentRepository.findById(legacy.getId()).orElseThrow().getPassword();
        assertThat(BCRYPT_HASH.matcher(hash).matches()).isTrue();
        assertThat(passwordEncoder.matches(priya.password(), hash)).isTrue();

        signIn(priya.email(), priya.password(), null).andExpect(authenticated());

        passwordMigration.run(null);
        assertThat(studentRepository.findById(legacy.getId()).orElseThrow().getPassword()).isEqualTo(hash);
    }

    @Test
    @DisplayName("TC-010-07 New profile is signed in automatically")
    public void newProfileIsSignedInAndShown() throws Exception {
        MvcResult created = mockMvc.perform(submit(butch))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        Long id = studentRepository.findByEmail(butch.email()).orElseThrow().getId();
        assertThat(created.getResponse().getRedirectedUrl()).isEqualTo("/students/" + id);

        MockHttpSession session = sessionOf(created);
        assertThat(page("/students/" + id, session)).contains(butch.firstName(), butch.lastName());
        mockMvc.perform(get("/profile").session(session))
                .andExpect(redirectedUrl("/students/" + id));
    }

    @Test
    @DisplayName("TC-010-08 Email already has a profile")
    public void emailThatAlreadyHasAProfileIsRefused() throws Exception {
        mockMvc.perform(submit(alex)).andExpect(status().is3xxRedirection());

        String page = mockMvc.perform(submit(withEmail(alex, "055555555", "Alex.Rivera@wsu.edu")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(studentRepository.count()).isEqualTo(1);
        int message = page.indexOf("A profile already exists for this email");
        assertThat(message).isGreaterThan(page.indexOf("name=\"email\""));
        assertThat(message).isLessThan(page.indexOf("name=\"major\""));
    }

    @Test
    @DisplayName("TC-010-09 Email is not a WSU email")
    public void emailThatIsNotAWsuEmailIsRefused() throws Exception {
        String page = mockMvc.perform(submit(withEmail(butch, butch.studentNumber(), "butch.cougar@gmail.com")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(studentRepository.count()).isZero();
        int message = page.indexOf("Use your WSU email");
        assertThat(message).isGreaterThan(page.indexOf("name=\"email\""));
        assertThat(message).isLessThan(page.indexOf("name=\"major\""));
    }

    @Test
    @DisplayName("TC-010-10 Signed-out visitor opens My student profile")
    public void signedOutMyStudentProfileGoesToSignInWithItsLinks() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        String signInPage = page("/login", null);
        String content = signInPage.substring(signInPage.indexOf("</header>"));
        assertThat(content).contains("href=\"/studentForm\"", "Create a student profile", "href=\"/\"");
    }

    @Test
    @DisplayName("TC-010-11 Sign out")
    public void signOutEndsTheSession() throws Exception {
        saveStudent(butch);
        MockHttpSession session = signedInSession(butch);

        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(redirectedUrl("/"));

        assertThat(session.isInvalid()).isTrue();
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("TC-010-12 Sign-in page shows email and password fields")
    public void signInPageShowsItsFieldsButtonAndLinks() throws Exception {
        String page = page("/login", null);

        assertThat(inputTag(page, "email")).contains("type=\"email\"");
        assertThat(inputTag(page, "password")).contains("type=\"password\"");
        assertThat(page).containsPattern("<button[^>]*type=\"submit\"[^>]*>Sign in</button>");
        String content = page.substring(page.indexOf("</header>"));
        assertThat(content).contains("href=\"/studentForm\"", "href=\"/\"");
    }

    @Test
    @DisplayName("TC-010-13 Invalid sign-in shows an error")
    public void invalidSignInShowsTheErrorOnceAndKeepsTheEmail() throws Exception {
        saveStudent(butch);

        MvcResult failed = signIn(butch.email(), "not-the-password", null)
                .andExpect(redirectedUrl("/login?error"))
                .andReturn();
        assertThat(failed.getResponse().getRedirectedUrl()).doesNotContain(butch.email());

        String page = page("/login?error", sessionOf(failed));
        assertThat(occurrences(page, SIGN_IN_ERROR)).isEqualTo(1);
        assertThat(inputTag(page, "email")).contains("value=\"" + butch.email() + "\"");
        assertThat(inputTag(page, "password")).doesNotContain("value=");
    }

    @Test
    @DisplayName("TC-010-14 Signed-in name shows in the header")
    public void signedInNameAndSignOutShowInTheHeader() throws Exception {
        Student student = saveStudent(butch);
        MockHttpSession session = signedInSession(butch);

        for (String url : List.of("/", "/clubs/search", "/students/" + student.getId())) {
            String header = header(page(url, session));
            assertThat(header).contains(butch.firstName(), "Sign out", "action=\"/logout\"");
            assertThat(header).doesNotContain("href=\"/login\"");
        }
        mockMvc.perform(get("/profile").session(session))
                .andExpect(redirectedUrl("/students/" + student.getId()));
    }

    @Test
    @DisplayName("TC-010-15 Signed-out header shows a sign-in link")
    public void signedOutHeaderShowsTheSignInLink() throws Exception {
        String header = header(page("/", null));

        assertThat(header).contains("href=\"/login\"", "Sign in");
        assertThat(header).doesNotContain("Sign out", "Signed in as");
    }

    @Test
    @DisplayName("TC-010-16 Sign out returns to the home page")
    public void signOutFromTheHeaderReturnsHomeSignedOut() throws Exception {
        saveStudent(butch);
        MockHttpSession session = signedInSession(butch);
        assertThat(header(page("/clubs/search", session))).contains("action=\"/logout\"");

        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        String header = header(page("/", null));
        assertThat(header).contains("href=\"/login\"");
        assertThat(header).doesNotContain(butch.firstName());
    }
}
