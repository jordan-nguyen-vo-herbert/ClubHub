package com.clubhub.ClubTests;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.ClubStatus;
import com.clubhub.entities.Member;
import com.clubhub.repository.ClubRepository;
import com.clubhub.security.StudentPrincipal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Registering a club needs a signed-in student with a saved profile, because the founder comes from the
// signed-in student (FR-002-01). So each request signs in with .with(user(...)) instead of @WithMockStudent
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
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
     * Verifies that the founder is always the signed-in student, even if
     * the request also carries another student's WSU ID (NFR-002-01).
     */
    @Test
    public void founderIsTheSignedInStudentEvenIfAnotherWsuIdIsSent() throws Exception {

        Student signedIn = createTestStudent("11111111");
        Student otherStudent = createTestStudent("22222222");

        mockMvc.perform(post("/clubForm")
                .with(csrf()) // the hidden token a real form includes, Spring Security rejects POSTs without it
                .with(user(new StudentPrincipal(signedIn))) // signs this request in as the saved student
                .param("clubName", "Founder Test Club")
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "Testing who the founder is")
                .param("contactEmail", "founder.test@wsu.edu")
                .param("founderStudentNumber", otherStudent.getStudentNumber()))

                .andExpect(status().is3xxRedirection());

        Club club = clubRepository.findByClubNameContainingIgnoreCase("Founder Test Club").get(0);

        assertEquals(
                signedIn.getStudentNumber(),
                club.getFounder().getStudentNumber(),
                "The founder should be the signed-in student, not the WSU ID in the request"
        );
        assertEquals(1, club.getMembers().size(), "The founder should be the only member");
        assertEquals(ClubRole.PRESIDENT, club.getMembers().get(0).getRole());
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
                .with(user(new StudentPrincipal(student)))
                .param("clubName", clubName)
                .param("areaOfInterest", AreaOfInterest.values()[0].name())
                .param("description", "Testing club registration " + i)
                .param("contactEmail", "test005@wsu.edu"))
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
                "The registered founder should match the signed-in student"
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