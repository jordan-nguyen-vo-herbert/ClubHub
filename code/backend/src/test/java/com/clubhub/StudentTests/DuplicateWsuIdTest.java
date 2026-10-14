package com.clubhub.StudentTests;

import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.clubhub.StudentTests.SampleStudents.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests duplicate WSU ID handling for TEST-003 (#18, FR-001).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DuplicateWsuIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Checks that a duplicate WSU ID displays an error, preserves the
     * other form answers, and does not create another student profile.
     *
     * @throws Exception if a request or response check fails
     */
    @Test
    public void reusingWsuIdShowsErrorAndKeepsWhatWasTyped() throws Exception {
        // Register Butch first
        StudentData butch = SampleStudents.ALL.get(0);
        mockMvc.perform(submit(butch));

        // Taylor tries to sign up with Butch's WSU ID
        StudentData taylor = new StudentData(
                butch.studentNumber(), "Taylor", "Smith",
                "taylor.smith@wsu.edu", "History",
                "", "", "pw12345");

        // The form should show the error and retain Taylor's answers
        mockMvc.perform(submit(taylor))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A profile already exists for this WSU ID")))
                .andExpect(content().string(containsString("value=\"Taylor\"")));

        // The duplicate submission must not create another profile.
        assertThat(studentRepository.count()).isEqualTo(1);
    }
}