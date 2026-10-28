package com.clubhub.SignInTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.PasswordMigration;
import com.clubhub.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.clubhub.StudentTests.SampleStudents.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * White-box tests for the sign-in back end, FR-010.1 (#49). Each test checks one
 * scenario from specs/features/sign-in/requirements.md.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignInBackEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordMigration passwordMigration;

    private final StudentData butch = SampleStudents.ALL.get(0);

    // Saves a student straight to the database with a hashed password, the way a created profile is stored
    private Student saveStudent(StudentData student) {
        return studentRepository.save(new Student(student.studentNumber(), student.firstName(), student.lastName(),
                student.email(), student.major(), null, null, passwordEncoder.encode(student.password())));
    }

    // Copies a sample student with a different email, to test the email rules
    private StudentData withEmail(StudentData student, String studentNumber, String email) {
        return new StudentData(studentNumber, student.firstName(), student.lastName(), email,
                student.major(), student.gender(), student.pronouns(), student.password());
    }

    /**
     * Scenario: Passwords are stored hashed.
     */
    @Test
    public void passwordsAreStoredHashed() throws Exception {
        mockMvc.perform(submit(butch)).andExpect(status().is3xxRedirection());

        String stored = studentRepository.findByEmail(butch.email()).orElseThrow().getPassword();
        assertThat(stored).isNotEqualTo(butch.password());
        assertThat(passwordEncoder.matches(butch.password(), stored)).isTrue();
    }

    /**
     * Scenario: Successful sign-in. With no page to return to, the student goes home.
     */
    @Test
    public void successfulSignInGoesToTheHomePage() throws Exception {
        saveStudent(butch);

        mockMvc.perform(formLogin("/login").userParameter("email").user(butch.email()).password(butch.password()))
                .andExpect(authenticated().withUsername(butch.email()))
                .andExpect(redirectedUrl("/"));
    }

    /**
     * Emails are stored in lowercase (FR-010-03), so capitalization doesn't matter at sign-in.
     */
    @Test
    public void signInIgnoresEmailCapitalization() throws Exception {
        saveStudent(butch);

        mockMvc.perform(formLogin("/login").userParameter("email").user("Butch.Cougar@WSU.edu").password(butch.password()))
                .andExpect(authenticated().withUsername(butch.email()));
    }

    /**
     * Scenario: Sign-in with invalid credentials (wrong password).
     */
    @Test
    public void wrongPasswordIsRejected() throws Exception {
        saveStudent(butch);

        mockMvc.perform(formLogin("/login").userParameter("email").user(butch.email()).password("not-the-password"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * An email with no profile gets exactly the same result as a wrong password (NFR-010-02).
     */
    @Test
    public void unknownEmailGetsTheSameErrorAsAWrongPassword() throws Exception {
        mockMvc.perform(formLogin("/login").userParameter("email").user("nobody@wsu.edu").password("anything"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * A failed sign-in keeps the typed email in the session, so the sign-in page can refill it (FR-010-07).
     */
    @Test
    public void failedSignInKeepsTheTypedEmail() throws Exception {
        mockMvc.perform(formLogin("/login").userParameter("email").user("typo@wsu.edu").password("anything"))
                .andExpect(request().sessionAttribute(SecurityConfig.LAST_EMAIL_ATTRIBUTE, "typo@wsu.edu"));
    }

    /**
     * The sign-in page has the fields and links fixed in design.md (FR-010-10).
     */
    @Test
    public void signInPageShowsTheEmailAndPasswordFields() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"email\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("Create a student profile")))
                .andExpect(content().string(containsString("Back to home")));
    }

    /**
     * After a failed sign-in, the page shows the error once and refills the typed email (FR-010-07).
     */
    @Test
    public void failedSignInShowsTheErrorAndRefillsTheEmail() throws Exception {
        MvcResult failed = mockMvc.perform(formLogin("/login").userParameter("email").user("typo@wsu.edu").password("anything"))
                .andExpect(redirectedUrl("/login?error"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) failed.getRequest().getSession(false);

        mockMvc.perform(get("/login?error").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A valid school account is required")))
                .andExpect(content().string(containsString("value=\"typo@wsu.edu\"")));

        // The email is handed over once, so it doesn't come back on a later visit
        assertThat(session.getAttribute(SecurityConfig.LAST_EMAIL_ATTRIBUTE)).isNull();
    }

    /**
     * Scenario: Signed-out visitor opens My student profile.
     */
    @Test
    public void signedOutVisitorIsSentToSignIn() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Scenario: Sign-in returns to the page the student wanted.
     */
    @Test
    public void signInReturnsToThePageTheStudentWanted() throws Exception {
        saveStudent(butch);

        // Opening /profile while signed out sends the student to sign in, and remembers /profile in the session
        MvcResult attempt = mockMvc.perform(get("/profile")).andReturn();
        MockHttpSession session = (MockHttpSession) attempt.getRequest().getSession(false);

        String target = mockMvc.perform(post("/login").with(csrf()).session(session)
                        .param("email", butch.email()).param("password", butch.password()))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();
        assertThat(target).contains("/profile");
    }

    /**
     * Scenario: New profile is signed in automatically.
     */
    @Test
    public void newProfileIsSignedInAutomatically() throws Exception {
        MvcResult created = mockMvc.perform(submit(butch)).andExpect(status().is3xxRedirection()).andReturn();
        MockHttpSession session = (MockHttpSession) created.getRequest().getSession(false);
        Long id = studentRepository.findByEmail(butch.email()).orElseThrow().getId();

        // With the browser's session, "My student profile" opens their own profile
        mockMvc.perform(get("/profile").session(session))
                .andExpect(redirectedUrl("/students/" + id));

        // Without it, the visitor is signed out, so the sign-in lives in the session and not in the test
        mockMvc.perform(get("/profile"))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Scenario: Email already has a profile (the check ignores capitalization).
     */
    @Test
    public void emailAlreadyHasAProfile() throws Exception {
        StudentData alex = SampleStudents.ALL.get(2);
        mockMvc.perform(submit(alex));

        mockMvc.perform(submit(withEmail(alex, "099999999", "Alex.Rivera@wsu.edu")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A profile already exists for this email")));
        assertThat(studentRepository.count()).isEqualTo(1);
    }

    /**
     * Scenario: Email is not a WSU email.
     */
    @Test
    public void emailMustBeAWsuEmail() throws Exception {
        mockMvc.perform(submit(withEmail(butch, butch.studentNumber(), "butch.cougar@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Use your WSU email")));
        assertThat(studentRepository.count()).isZero();
    }

    /**
     * Scenario: Existing plain-text passwords are hashed at startup.
     */
    @Test
    public void existingPlainTextPasswordsAreHashedAtStartup() throws Exception {
        // A profile saved before sign-in existed, with its password stored as typed
        Student legacy = studentRepository.save(new Student("055555555", "Legacy", "Student",
                "legacy.student@wsu.edu", "History", null, null, "plainPassword1"));

        passwordMigration.run(null);
        String hash = studentRepository.findById(legacy.getId()).orElseThrow().getPassword();
        assertThat(hash).isNotEqualTo("plainPassword1");
        assertThat(passwordEncoder.matches("plainPassword1", hash)).isTrue();

        // Running it again leaves the hash alone
        passwordMigration.run(null);
        assertThat(studentRepository.findById(legacy.getId()).orElseThrow().getPassword()).isEqualTo(hash);

        // And the student still signs in with their original password
        mockMvc.perform(formLogin("/login").userParameter("email").user("legacy.student@wsu.edu").password("plainPassword1"))
                .andExpect(authenticated());
    }

    /**
     * Scenario: Sign out.
     */
    @Test
    public void signOutEndsTheSession() throws Exception {
        saveStudent(butch);
        MvcResult signedIn = mockMvc.perform(post("/login").with(csrf())
                        .param("email", butch.email()).param("password", butch.password()))
                .andExpect(redirectedUrl("/"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) signedIn.getRequest().getSession(false);

        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(redirectedUrl("/"));
        assertThat(session.isInvalid()).isTrue();
    }
}
