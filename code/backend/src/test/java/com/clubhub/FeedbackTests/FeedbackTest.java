package com.clubhub.FeedbackTests;

import com.clubhub.entities.Feedback;
import com.clubhub.entities.FeedbackCategory;
import com.clubhub.entities.Student;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Feedback constructor (FR-008.1): one piece of feedback per category, each sent by Jordan.
 * Plain JUnit with no Spring or database, because the constructor and getters don't need either.
 */
public class FeedbackTest {

    private Student jordan;

    @BeforeEach
    public void createJordan() {
        // The student from the Selenium test, built as the Student entity the Feedback constructor takes.
        // Feedback doesn't use gender, pronouns or the password, so those are left empty or a placeholder
        jordan = new Student("11983460", "Jordan", "Nguyen-Vo Herbert", "j.nguyen-voherbert@wsu.edu",
                "Computer Science", null, null, "placeholder-password");
    }

    @Test
    public void suggestionFromJordan() {
        createAndCheck(FeedbackCategory.SUGGESTION, "Add accessibility elements");
    }

    @Test
    public void bugReportFromJordan() {
        createAndCheck(FeedbackCategory.BUG_REPORT, "The club search page does not load");
    }

    @Test
    public void otherFromJordan() {
        createAndCheck(FeedbackCategory.OTHER, "test");
    }

    // Creates feedback from Jordan, then checks each getter returns what the constructor was given
    private void createAndCheck(FeedbackCategory category, String message) {
        Instant before = Instant.now();
        Feedback feedback = new Feedback(category, message, jordan);
        Instant after = Instant.now();

        assertEquals(category, feedback.getCategory());
        assertEquals(message, feedback.getMessage());
        assertSame(jordan, feedback.getSubmitter()); // the same Student object, not a copy
        assertNull(feedback.getId()); // the database assigns the ID when the feedback is saved

        // Stamped between the two clock readings. Equal to either one still passes,
        // because the clock may not tick between the three lines above
        Instant submittedAt = feedback.getSubmittedAt();
        assertNotNull(submittedAt);
        assertFalse(submittedAt.isBefore(before), "submittedAt is earlier than the constructor call");
        assertFalse(submittedAt.isAfter(after), "submittedAt is later than the constructor call");
    }
}
