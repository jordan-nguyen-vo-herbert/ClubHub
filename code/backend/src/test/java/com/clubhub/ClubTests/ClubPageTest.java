package com.clubhub.ClubTests;

import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClubPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Create a student to serve as the club's founder.
    private Student createTestStudent() {
        Student student = new Student(
                "76543210",
                "Test",
                "Student",
                "clubpage-test@wsu.edu",
                "Computer Science",
                null,
                null,
                "testPassword"
        );

        return studentRepository.save(student);
    }

    /**
     * Verifies that a club's page is displayed after the club
     * has been created and saved.
     */
    @Test
    public void clubPageDisplaysAfterClubIsCreated() throws Exception {

        // Arrange: create and save a student and club.
        Student founder = createTestStudent();

        Club club = new Club(
                "Club Page Test Club",
                AreaOfInterest.values()[0],
                "Testing whether the club page displays",
                founder
        );

        Club savedClub = clubRepository.save(club);

        // Act and assert: open the saved club's page.
        mockMvc.perform(get("/clubs/" + savedClub.getClubID()))
                .andExpect(status().isOk())
                .andExpect(view().name("club"))
                .andExpect(model().attributeExists("club"))
                .andExpect(content().string(
                        containsString("Club Page Test Club")));
    }

    /**
     * Verifies that a 404 error is returned when trying to access
     * a club page that doesn't exist.
     */
    @Test
    public void missingClubIdDisplays404Page() throws Exception {

    long missingClubId = Long.MAX_VALUE;

    assertFalse(clubRepository.existsById(missingClubId));

    mockMvc.perform(get("/clubs/" + missingClubId))
            .andExpect(status().isNotFound());
    }
}