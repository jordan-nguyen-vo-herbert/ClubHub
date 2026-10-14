package com.clubhub.testdata;

import com.clubhub.dto.StudentForm;
import com.clubhub.entities.AreaOfInterest;
import com.clubhub.entities.Club;
import com.clubhub.entities.ClubRole;
import com.clubhub.entities.Gender;
import com.clubhub.entities.Pronouns;
import com.clubhub.entities.Student;
import com.clubhub.repository.ClubRepository;
import com.clubhub.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// TEST-001: the five students and five clubs every other test case is written against.
// Shared on purpose, the student cases (TEST-002..004) and the club cases (TEST-005..009)
// assert against the same names, WSU IDs and areas, so a failure means the code changed,
// not that two people picked different data.
public final class TestData {

    private TestData() {} // helpers only, never instantiated

    // --- Students ---------------------------------------------------------
    // WSU IDs are Strings and some start with 0 on purpose, that's why Student stores them as text.
    // Jae Park has no gender or pronouns, those fields are optional and the profile page hides them.

    public static final StudentSpec AVA =
            new StudentSpec("011223344", "Ava", "Nguyen", "ava.nguyen@wsu.edu",
                    "Computer Science", Gender.WOMAN, Pronouns.SHE_HER, "AvaPass!23");
    public static final StudentSpec MARCUS =
            new StudentSpec("011998877", "Marcus", "Hill", "marcus.hill@wsu.edu",
                    "Mechanical Engineering", Gender.MAN, Pronouns.HE_HIM, "MarcusPass!23");
    public static final StudentSpec RILEY =
            new StudentSpec("012345678", "Riley", "Okafor", "riley.okafor@wsu.edu",
                    "Data Analytics", Gender.NONBINARY, Pronouns.THEY_THEM, "RileyPass!23");
    public static final StudentSpec SOFIA =
            new StudentSpec("010101010", "Sofia", "Ramirez", "sofia.ramirez@wsu.edu",
                    "Business Administration", Gender.WOMAN, Pronouns.SHE_THEY, "SofiaPass!23");
    public static final StudentSpec JAE =
            new StudentSpec("013579111", "Jae", "Park", "jae.park@wsu.edu",
                    "Animal Sciences", null, null, "JaePass!23");

    // A WSU ID that is deliberately not on record, for TEST-006 (unknown founder)
    public static final String UNKNOWN_STUDENT_NUMBER = "099999999";

    public static List<StudentSpec> students() {
        return List.of(AVA, MARCUS, RILEY, SOFIA, JAE);
    }

    // Used by @ParameterizedTest in StudentProfileCreationTests, JUnit needs a Stream
    public static Stream<StudentSpec> studentStream() {
        return students().stream();
    }

    // --- Clubs ------------------------------------------------------------
    // "Chess Club" exists so a search for "chess" has a partial match (TEST-008).
    // Two clubs share COMPUTER_SCIENCE so a search by area returns more than one row (TEST-009).

    public static final ClubSpec CHESS =
            new ClubSpec("Chess Club", AreaOfInterest.GAMING,
                    "Casual and rated chess, weekly ladder nights in the CUB.", RILEY.studentNumber());
    public static final ClubSpec CODING =
            new ClubSpec("Cougar Coding Club", AreaOfInterest.COMPUTER_SCIENCE,
                    "Project nights, interview prep and a yearly hackathon team.", AVA.studentNumber());
    public static final ClubSpec CYBER =
            new ClubSpec("Cyber Cougars", AreaOfInterest.COMPUTER_SCIENCE,
                    "Capture-the-flag practice and security reading group.", MARCUS.studentNumber());
    public static final ClubSpec BUSINESS =
            new ClubSpec("Business Leaders Network", AreaOfInterest.BUSINESS,
                    "Case competitions, resume reviews and alumni panels.", SOFIA.studentNumber());
    public static final ClubSpec PRE_VET =
            new ClubSpec("Pre-Vet Student Association", AreaOfInterest.PRE_VET,
                    "Clinic shadowing, WSU vet school info sessions and animal welfare volunteering.",
                    JAE.studentNumber());

    public static List<ClubSpec> clubs() {
        return List.of(CHESS, CODING, CYBER, BUSINESS, PRE_VET);
    }

    // --- Seeding ----------------------------------------------------------

    // Saves all five students and returns the stored copies, which have their database ids filled in
    public static List<Student> seedStudents(StudentRepository studentRepository) {
        List<Student> saved = new ArrayList<>();
        for (StudentSpec spec : students()) {
            saved.add(studentRepository.save(spec.toEntity()));
        }
        return saved;
    }

    // Saves all five clubs, each with its founder as PRESIDENT, the same way ClubController does.
    // Students must already be seeded, the founder is looked up by WSU ID.
    public static List<Club> seedClubs(StudentRepository studentRepository, ClubRepository clubRepository) {
        List<Club> saved = new ArrayList<>();
        for (ClubSpec spec : clubs()) {
            Student founder = studentRepository.findByStudentNumber(spec.founderStudentNumber())
                    .orElseThrow(() -> new IllegalStateException(
                            "Seed the students before the clubs, no student with WSU ID " + spec.founderStudentNumber()));
            Club club = new Club(spec.clubName(), spec.areaOfInterest(), spec.description(), founder);
            club.addMember(founder, ClubRole.PRESIDENT); // founder is the first member, cascade saves the membership
            saved.add(clubRepository.save(club));
        }
        return saved;
    }

    // Everything at once, for the club cases that need both
    public static Seeded seedAll(StudentRepository studentRepository, ClubRepository clubRepository) {
        List<Student> students = seedStudents(studentRepository);
        List<Club> clubs = seedClubs(studentRepository, clubRepository);
        return new Seeded(students, clubs);
    }

    public record Seeded(List<Student> students, List<Club> clubs) {}

    // One student's test data, with helpers to turn it into whichever shape a test needs
    public record StudentSpec(String studentNumber, String firstName, String lastName, String email,
                              String major, Gender gender, Pronouns pronouns, String password) {

        // For tests that save straight through the repository
        public Student toEntity() {
            return new Student(studentNumber, firstName, lastName, email, major, gender, pronouns, password);
        }

        // For tests that save through the real StudentController
        public StudentForm toForm() {
            StudentForm form = new StudentForm();
            form.setStudentNumber(studentNumber);
            form.setFirstName(firstName);
            form.setLastName(lastName);
            form.setEmail(email);
            form.setMajor(major);
            form.setGender(gender);
            form.setPronouns(pronouns);
            form.setPassword(password);
            return form;
        }

        public String fullName() {
            return firstName + " " + lastName;
        }

        @Override
        public String toString() {
            return fullName() + " (" + studentNumber + ")"; // names the parameterized test cases in the report
        }
    }

    public record ClubSpec(String clubName, AreaOfInterest areaOfInterest, String description,
                           String founderStudentNumber) {
        @Override
        public String toString() {
            return clubName;
        }
    }
}
