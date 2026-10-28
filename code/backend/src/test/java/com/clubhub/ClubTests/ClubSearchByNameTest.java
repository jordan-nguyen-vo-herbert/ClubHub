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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // club search needs a signed-in student (FR-010-05), a stand-in test user is enough here
public class ClubSearchByNameTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Create a student to serve as the founder of the test clubs.
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

    // Create and save a club for testing.
    private Club createTestClub(
            String clubName,
            AreaOfInterest area,
            Student founder) {

        Club club = new Club(
                clubName,
                area,
                "Test club for name search",
                founder
        );

        return clubRepository.save(club);
    }

    /**
     * Verifies that searching by name displays matching clubs
     * and excludes clubs that don't match the search.
     */
    @Test
    public void searchByNameDisplaysMatchingClubs() throws Exception {

        // Arrange: create clubs with different names.
        Student founder = createTestStudent("87654321");

        AreaOfInterest area = AreaOfInterest.values()[0];

        createTestClub("Chess Club", area, founder);
        createTestClub("Advanced Chess Club", area, founder);
        createTestClub("Photography Club", area, founder);

        // Act and assert: search for clubs containing "Chess".
        mockMvc.perform(get("/clubs/search/name")
                        .param("name", "Chess"))
                .andExpect(status().isOk())
                .andExpect(view().name("clubs"))
                .andExpect(model().attributeExists("clubs"))
                .andExpect(content().string(
                        containsString("Chess Club")))
                .andExpect(content().string(
                        containsString("Advanced Chess Club")))
                .andExpect(content().string(
                        not(containsString("Photography Club"))));
    }

    /**
     * Verifies that capitalization doesn't matter when searching by name.
     */

    /**
     * TEST-008: Verifies that club name searches are case-insensitive.
    */
    @Test
    public void searchByNameIgnoresCapitalization() throws Exception {

        // Arrange: create clubs with different names.
        Student founder = createTestStudent("87654322");

        AreaOfInterest area = AreaOfInterest.values()[0];

        createTestClub("Chess Club", area, founder);
        createTestClub("Advanced Chess Club", area, founder);
        createTestClub("Photography Club", area, founder);

        // Act and assert: search using lowercase "chess".
        mockMvc.perform(get("/clubs/search/name")
                    .param("name", "chess"))
            .andExpect(status().isOk())
            .andExpect(view().name("clubs"))
            .andExpect(model().attributeExists("clubs"))
            .andExpect(content().string(
                    containsString("Chess Club")))
            .andExpect(content().string(
                    containsString("Advanced Chess Club")))
            .andExpect(content().string(
                    not(containsString("Photography Club"))));
   }

}