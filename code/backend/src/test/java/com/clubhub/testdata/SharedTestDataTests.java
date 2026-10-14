package com.clubhub.testdata;

import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.ClubStatus;
import com.clubhub.entities.Member;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// TEST-001: the shared test data loads into a clean database.
// If this fails, every other test case is standing on broken ground, so run it first.
@SpringBootTest
@Transactional // rolled back after the test, the next test class starts from an empty database
@DisplayName("TEST-001: Shared test data - 5 students, 5 clubs")
class SharedTestDataTests {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ClubRepository clubRepository;

    @Test
    @DisplayName("All five students and five clubs are saved with database ids")
    void seedsFiveStudentsAndFiveClubs() {
        TestData.Seeded seeded = TestData.seedAll(studentRepository, clubRepository);

        assertThat(seeded.students()).hasSize(5);
        assertThat(seeded.clubs()).hasSize(5);
        // The database assigns the ids, a null id means the save never happened
        assertThat(seeded.students()).allSatisfy(student -> assertThat(student.getId()).isNotNull());
        assertThat(seeded.clubs()).allSatisfy(club -> assertThat(club.getClubID()).isNotNull());
    }

    @Test
    @DisplayName("Every student can be found again by WSU ID")
    void studentsAreFindableByStudentNumber() {
        TestData.seedStudents(studentRepository);

        for (TestData.StudentSpec spec : TestData.students()) {
            Student found = studentRepository.findByStudentNumber(spec.studentNumber())
                    .orElseThrow(() -> new AssertionError("No student saved for WSU ID " + spec.studentNumber()));
            assertThat(found.getFirstName()).isEqualTo(spec.firstName());
            assertThat(found.getLastName()).isEqualTo(spec.lastName());
            assertThat(found.getEmail()).isEqualTo(spec.email());
            assertThat(found.getMajor()).isEqualTo(spec.major());
            assertThat(found.getGender()).isEqualTo(spec.gender());
            assertThat(found.getPronouns()).isEqualTo(spec.pronouns());
        }
    }

    @Test
    @DisplayName("The unknown-founder WSU ID is deliberately not on record")
    void unknownStudentNumberIsNotSeeded() {
        TestData.seedStudents(studentRepository);

        // TEST-006 relies on this ID staying absent, so nobody can accidentally add it to the seed data
        assertThat(studentRepository.findByStudentNumber(TestData.UNKNOWN_STUDENT_NUMBER)).isEmpty();
    }

    @Test
    @DisplayName("Each club starts PENDING with its founder as the president")
    void clubsStartPendingWithFounderAsPresident() {
        TestData.seedAll(studentRepository, clubRepository);

        for (TestData.ClubSpec spec : TestData.clubs()) {
            Club club = clubRepository.findByClubNameContainingIgnoreCase(spec.clubName()).stream()
                    .filter(c -> c.getClubName().equals(spec.clubName()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No club saved named " + spec.clubName()));

            assertThat(club.getAreaOfInterest()).isEqualTo(spec.areaOfInterest());
            assertThat(club.getDescription()).isEqualTo(spec.description());
            // Club approval isn't implemented yet, so every club is still an application
            assertThat(club.getStatus()).isEqualTo(ClubStatus.PENDING);
            assertThat(club.getDateApproved()).isNull();

            assertThat(club.getFounder().getStudentNumber()).isEqualTo(spec.founderStudentNumber());
            assertThat(club.getMembers()).hasSize(1);
            Member president = club.getMembers().get(0);
            assertThat(president.getRole()).isEqualTo(ClubRole.PRESIDENT);
            assertThat(president.getStudent().getStudentNumber()).isEqualTo(spec.founderStudentNumber());
        }
    }
}
