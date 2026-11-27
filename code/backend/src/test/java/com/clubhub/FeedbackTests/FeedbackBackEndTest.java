package com.clubhub.FeedbackTests;

import com.clubhub.StudentTests.SampleStudents;
import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.entities.Feedback;
import com.clubhub.entities.FeedbackCategory;
import com.clubhub.entities.Student;
import com.clubhub.repository.FeedbackRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
 * White-box tests for the feedback back end, FR-008.1 (#64). Each test checks one scenario from
 * specs/features/feedback/requirements.md. Runs on H2 through the "test" profile, not the local PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class FeedbackBackEndTest {

    private static final String CONFIRMATION = "Thanks, your feedback was received.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private final StudentData butch = SampleStudents.ALL.get(0);
    private final StudentData alex = SampleStudents.ALL.get(2);

    // Saves a student straight to the database, the way a created profile is stored
    private Student saveStudent(StudentData student) {
        return studentRepository.save(new Student(student.studentNumber(), student.firstName(), student.lastName(),
                student.email(), student.major(), null, null, student.password()));
    }

    // Submits the feedback form as this student, the way the browser does once they're signed in
    private ResultActions sendFeedback(Student student, String category, String message) throws Exception {
        return mockMvc.perform(post("/feedback").with(csrf()).with(user(new StudentPrincipal(student)))
                .param("category", category)
                .param("message", message));
    }

    /**
     * FR-008-01: the form opens for a signed-in user, with every category in the dropdown.
     */
    @Test
    public void formOpensForASignedInUser() throws Exception {
        Student student = saveStudent(butch);

        mockMvc.perform(get("/feedback").with(user(new StudentPrincipal(student))))
                .andExpect(status().isOk())
                .andExpect(view().name("feedback"))
                .andExpect(content().string(containsString("Send Feedback")))
                .andExpect(content().string(containsString("Bug report")))
                .andExpect(content().string(containsString("Suggestion")))
                .andExpect(content().string(not(containsString(CONFIRMATION))));
    }

    /**
     * Scenario: Successfully submit feedback.
     */
    @Test
    public void validFeedbackIsStoredAndConfirmed() throws Exception {
        Student student = saveStudent(butch);
        long feedbackBefore = feedbackRepository.count();

        sendFeedback(student, FeedbackCategory.SUGGESTION.name(), "  Please add a club calendar.  ")
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feedback"))
                .andExpect(flash().attribute("feedbackSent", true));

        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore + 1);
        Feedback stored = feedbackRepository.findAllByOrderBySubmittedAtDesc().get(0);
        assertThat(stored.getCategory()).isEqualTo(FeedbackCategory.SUGGESTION);
        assertThat(stored.getMessage()).isEqualTo("Please add a club calendar."); // spaces around it trimmed
        assertThat(stored.getSubmitter().getId()).isEqualTo(student.getId());
        assertThat(stored.getSubmittedAt()).isNotNull();

        // The page the redirect leads to shows the confirmation, once
        mockMvc.perform(get("/feedback").with(user(new StudentPrincipal(student))).flashAttr("feedbackSent", true))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CONFIRMATION)));
    }

    /**
     * Scenario: Submit an empty feedback form.
     */
    @Test
    public void emptyFormIsRefusedWithAMessageForEachField() throws Exception {
        Student student = saveStudent(butch);
        long feedbackBefore = feedbackRepository.count();

        sendFeedback(student, "", "")
                .andExpect(status().isOk())
                .andExpect(view().name("feedback"))
                .andExpect(model().attributeHasFieldErrors("feedbackForm", "category", "message"))
                .andExpect(content().string(containsString("Choose a category")))
                .andExpect(content().string(containsString("Enter your feedback")))
                .andExpect(content().string(not(containsString(CONFIRMATION))));

        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore);
    }

    /**
     * Scenario: Submit an empty feedback form, with a message that is only spaces.
     */
    @Test
    public void messageOfOnlySpacesIsRefused() throws Exception {
        Student student = saveStudent(butch);
        long feedbackBefore = feedbackRepository.count();

        sendFeedback(student, FeedbackCategory.OTHER.name(), "     ")
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("feedbackForm", "message"))
                .andExpect(content().string(containsString("Enter your feedback")));

        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore);
    }

    /**
     * Scenario: Message is too long. One character over the limit is refused and kept in the form,
     * exactly the limit is stored.
     */
    @Test
    public void messageOverTheLimitIsRefusedAndKept() throws Exception {
        Student student = saveStudent(butch);
        long feedbackBefore = feedbackRepository.count();
        String tooLong = "x".repeat(1001);

        sendFeedback(student, FeedbackCategory.BUG_REPORT.name(), tooLong)
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("feedbackForm", "message"))
                .andExpect(content().string(containsString("Keep your feedback to 1000 characters or fewer")))
                .andExpect(content().string(containsString(tooLong)));
        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore);

        sendFeedback(student, FeedbackCategory.BUG_REPORT.name(), "x".repeat(1000))
                .andExpect(status().is3xxRedirection());
        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore + 1);
    }

    /**
     * Scenario: Category is not in the list. The message comes from messages.properties,
     * never Spring's default, which names a Java class (NFR-008-03).
     */
    @Test
    public void unknownCategoryIsRefusedWithAFriendlyMessage() throws Exception {
        Student student = saveStudent(butch);
        long feedbackBefore = feedbackRepository.count();

        sendFeedback(student, "NOT_A_CATEGORY", "The search page is slow.")
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("feedbackForm", "category"))
                .andExpect(content().string(containsString("Choose a category from the list")))
                .andExpect(content().string(not(containsString("FeedbackCategory"))))
                .andExpect(content().string(not(containsString("java."))))
                .andExpect(content().string(containsString("The search page is slow.")));

        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore);
    }

    /**
     * Scenario: Feedback is stored under the signed-in user, even when the form names another student.
     */
    @Test
    public void senderIsAlwaysTheSignedInStudent() throws Exception {
        Student signedIn = saveStudent(butch);
        Student other = saveStudent(alex);

        mockMvc.perform(post("/feedback").with(csrf()).with(user(new StudentPrincipal(signedIn)))
                        .param("category", FeedbackCategory.OTHER.name())
                        .param("message", "Sent by Butch")
                        .param("submitter", other.getId().toString())
                        .param("submitter.id", other.getId().toString()))
                .andExpect(status().is3xxRedirection());

        List<Feedback> stored = feedbackRepository.findAllByOrderBySubmittedAtDesc();
        assertThat(stored).isNotEmpty();
        assertThat(stored.get(0).getSubmitter().getId()).isEqualTo(signedIn.getId());
    }

    /**
     * Scenario: Signed-out visitor opens the feedback form.
     */
    @Test
    public void signedOutVisitorOpeningTheFormIsSentToSignIn() throws Exception {
        mockMvc.perform(get("/feedback"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Scenario: Signed-out visitor submits the feedback form, with a valid CSRF token. Nothing is stored.
     */
    @Test
    public void signedOutSubmissionIsNotStoredAndGoesToSignIn() throws Exception {
        long feedbackBefore = feedbackRepository.count();

        mockMvc.perform(post("/feedback").with(csrf())
                        .param("category", FeedbackCategory.OTHER.name())
                        .param("message", "This should never be stored"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertThat(feedbackRepository.count()).isEqualTo(feedbackBefore);
    }
}
