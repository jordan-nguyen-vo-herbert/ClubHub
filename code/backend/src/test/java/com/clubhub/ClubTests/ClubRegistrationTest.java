package com.clubhub.ClubTests;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClubRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    // Helper method to create a student directly in the test database.
    private Student createTestStudent(String studentNumber) {
        Student student = new Student(
                studentNumber,
                "Test",
                "Student",
                "test" + studentNumber + "@wsu.edu",
                "Computer Science",
                null,
                null,
                "testPassword"
        );

        return studentRepository.save(student);
    }

    /**
     * Verifies that club registration fails if the founder's
     * WSU student number doesn't exist.
     */
    @Test
    public void registrationFailsWithInvalidStudentID() throws Exception {

        mockMvc.perform(post("/clubForm")
                .param("clubName", "Invalid ID Test Club")
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "Testing invalid student ID")
                .param("founderStudentNumber", "INVALID-TEST-ID"))

                .andExpect(status().isOk())
                .andExpect(view().name("clubForm"))
                .andExpect(model().attributeHasFieldErrors(
                        "clubForm", "founderStudentNumber"));
    }

    /**
     * Verifies that a student with an existing profile
     * can successfully register a club.
     */
    @Test
    public void registrationSucceedsWithValidStudentID() throws Exception {

        Student student = createTestStudent("12345678");

        mockMvc.perform(post("/clubForm")
                .param("clubName", "Valid ID Test Club")
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "Testing valid student ID")
                .param("founderStudentNumber", student.getStudentNumber()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clubs/*"));
    }
}