package com.clubhub.StudentTests;

import com.clubhub.StudentTests.SampleStudents.StudentData;
import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.clubhub.StudentTests.SampleStudents.submit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests student profile creation for TEST-002 (#17, FR-001).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // profile pages need a signed-in student (FR-010-05), a stand-in test user is enough here
public class StudentProfileCreationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Checks that each sample student is saved and redirected to a
     * profile page that displays their name.
     *
     * @throws Exception if a request or response check fails
     */
    @Test
    public void allFiveStudentsSaveAndGoToProfilePage() throws Exception {
        for (StudentData student : SampleStudents.ALL) {
            // Submit the form and capture the profile URL.
            String profileUrl = mockMvc.perform(submit(student))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/students/*"))
                    .andReturn().getResponse().getRedirectedUrl();

            // Check that the student was saved.
            assertThat(studentRepository.findByStudentNumber(student.studentNumber())).isPresent();

            // Check that the profile page displays the student's name.
            mockMvc.perform(get(profileUrl))
                    .andExpect(status().isOk())
                    .andExpect(content().string(
                            containsString(student.firstName() + " " + student.lastName())));
        }

        // Check that all five profiles were added.
        assertThat(studentRepository.count()).isEqualTo(5);
    }
}