package com.clubhub.ClubTests;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.ClubStatus;
import com.clubhub.entities.Member;
import com.clubhub.repository.ClubRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // registering a club needs a signed-in student (FR-010-05), a stand-in test user is enough here
public class ClubRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired 
    private ClubRepository clubRepository;

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
                .with(csrf()) // the hidden token a real form includes, Spring Security rejects POSTs without it
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
public void registrationCreatesFivePendingClubsWithFounderAsPresident()
        throws Exception {

    // Create the founder's student profile.
    Student student = createTestStudent("12345678");

    // Record the number of clubs before registration.
    long initialClubCount = clubRepository.count();

    // Register five clubs.
    for (int i = 1; i <= 5; i++) {
        String clubName = "TEST-005 Club " + i;

        mockMvc.perform(post("/clubForm")
                .with(csrf())
                .param("clubName", clubName)
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "Testing club registration " + i)
                .param("founderStudentNumber",
                        student.getStudentNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clubs/*"));
    }

    // Verify that exactly five clubs were created.
    assertEquals(
            initialClubCount + 5,
            clubRepository.count(),
            "Five new clubs should be registered"
    );

    // Verify every registered club.
    for (int i = 1; i <= 5; i++) {
        String clubName = "TEST-005 Club " + i;

        List<Club> matchingClubs =
                clubRepository.findByClubNameContainingIgnoreCase(clubName);

        assertEquals(
                1,
                matchingClubs.size(),
                "The club should exist exactly once: " + clubName
        );

        Club club = matchingClubs.get(0);

        // Verify the founder.
        assertNotNull(club.getFounder());
        assertEquals(
                student.getStudentNumber(),
                club.getFounder().getStudentNumber(),
                "The registered founder should match the submitted student"
        );

        // Verify the default Pending status.
        assertEquals(
                ClubStatus.PENDING,
                club.getStatus(),
                "New club applications should have Pending status"
        );

        // Verify the founder is a member with the President role.
        List<Member> members = club.getMembers();

        assertTrue(
                members.stream().anyMatch(member ->
                        member.getStudent().getStudentNumber()
                                .equals(student.getStudentNumber())
                                && member.getRole() == ClubRole.PRESIDENT
                ),
                "The founder should be a member with the President role"
        );
    }
}
}