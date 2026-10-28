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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // club search needs a signed-in student (FR-010-05), a stand-in test user is enough here
public class ClubSearchByAreaTest {

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
                "Test club for area search",
                founder
        );

        return clubRepository.save(club);
    }

    /**
     * Verifies that searching by area displays matching clubs
     * and excludes clubs from other areas.
     */
    @Test
    public void searchByAreaDisplaysMatchingClubs() throws Exception {

        // Arrange: create clubs in different areas.
        Student founder = createTestStudent("87654321");

        AreaOfInterest areaToSearch = AreaOfInterest.values()[0];

        AreaOfInterest otherArea = null;
        for (AreaOfInterest area : AreaOfInterest.values()) {
            if (area != areaToSearch) {
                otherArea = area;
                break;
            }
        }

        assertNotNull(otherArea,
                "AreaOfInterest must contain at least two values.");

        createTestClub("First Matching Club", areaToSearch, founder);
        createTestClub("Second Matching Club", areaToSearch, founder);
        createTestClub("Different Area Club", otherArea, founder);

        // Act and assert: simulate searching by area on the website.
        mockMvc.perform(get("/clubs/search/area")
                        .param("area", areaToSearch.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("clubs"))
                .andExpect(model().attributeExists("clubs"))
                .andExpect(content().string(
                        containsString("First Matching Club")))
                .andExpect(content().string(
                        containsString("Second Matching Club")))
                .andExpect(content().string(
                        not(containsString("Different Area Club"))));
    }


    /**
     * Verifies that searching an area with no clubs
     * displays no results.
     */
    @Test
    public void searchByAreaWithNoClubsDisplaysNoResults() throws Exception {

        // Arrange: create a founder and select two different areas.
        Student founder = createTestStudent("87654322");

        AreaOfInterest areaWithClubs = AreaOfInterest.values()[0];

        AreaOfInterest emptyArea = null;
        for (AreaOfInterest area : AreaOfInterest.values()) {
            if (area != areaWithClubs) {
                emptyArea = area;
                break;
            }
        }

        assertNotNull(emptyArea,
            "AreaOfInterest must contain at least two values.");

        // Create clubs only in the first area.
        createTestClub("Chess Club", areaWithClubs, founder);
        createTestClub("Photography Club", areaWithClubs, founder);

        // Act and assert: search the area with no clubs.
        mockMvc.perform(get("/clubs/search/area")
                    .param("area", emptyArea.name()))
            .andExpect(status().isOk())
            .andExpect(view().name("clubs"))
            .andExpect(model().attributeExists("clubs"))
            .andExpect(content().string(
                    not(containsString("Chess Club"))))
            .andExpect(content().string(
                    not(containsString("Photography Club"))));
    }

}