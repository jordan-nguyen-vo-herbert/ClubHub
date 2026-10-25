package com.clubhub.StudentTests;

import com.clubhub.StudentTests.SampleStudents.StudentData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.clubhub.StudentTests.SampleStudents.submit;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests student profile pages for TEST-004 (#19, FR-001)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // profile pages need a signed-in student (FR-010-05), a stand-in test user is enough here
public class StudentProfilePageTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that a student's profile displays their information
     * without exposing their password
     *
     * @throws Exception if a request or response check fails
     */
    @Test
    public void profileShowsDetailsButNeverThePassword() throws Exception {
        // Create Butch's profile and get its URL
        StudentData butch = SampleStudents.ALL.get(0);
        String profileUrl = mockMvc.perform(submit(butch))
                .andReturn().getResponse().getRedirectedUrl();

        // Check the profile details and make sure the password is hidden
        mockMvc.perform(get(profileUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(butch.firstName() + " " + butch.lastName()))) // name
                .andExpect(content().string(containsString(butch.studentNumber()))) // WSU ID
                .andExpect(content().string(containsString(butch.email()))) // email
                .andExpect(content().string(containsString(butch.major()))) // major
                .andExpect(content().string(containsString("Man"))) // gender (display text)
                .andExpect(content().string(containsString("he/him"))) // pronouns (display text)
                .andExpect(content().string(not(containsString(butch.password())))); // password hidden
    }

    /**
     * Verifies that requesting a profile that does not exist
     * returns a 404 Not Found response
     *
     * @throws Exception if the request or response check fails
     */
    @Test
    public void missingStudentShows404() throws Exception {
        mockMvc.perform(get("/students/999999"))
                .andExpect(status().isNotFound());
    }
}