package com.clubhub.student;

import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.testdata.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// TEST-002 (FR-001): all 5 student profiles save and land on the profile page.
// Goes through the real form POST, not the repository, so the controller, the binding and the
// redirect are all covered.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("TEST-002: Student profile creation (FR-001)")
class StudentProfileCreationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("The create-profile form opens with an empty form object")
    void formPageOpensWithBlankForm() throws Exception {
        mockMvc.perform(get("/studentForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentForm"))
                .andExpect(model().attributeExists("studentForm"))
                // the dropdowns have to be there or the page renders without options
                .andExpect(model().attributeExists("genderOptions", "pronounOptions"));
    }

    @ParameterizedTest(name = "{0} saves and redirects to the profile page")
    @MethodSource("com.clubhub.testdata.TestData#studentStream")
    void profileSavesAndRedirectsToProfilePage(TestData.StudentSpec spec) throws Exception {
        MvcResult result = mockMvc.perform(submit(spec))
                .andExpect(status().is3xxRedirection())
                // the id is generated, so match the shape of the URL
                .andExpect(redirectedUrlPattern("/students/*"))
                .andReturn();

        // The student is in the database with everything that was typed
        Student saved = studentRepository.findByStudentNumber(spec.studentNumber())
                .orElseThrow(() -> new AssertionError("Profile was not saved for " + spec));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(spec.firstName());
        assertThat(saved.getLastName()).isEqualTo(spec.lastName());
        assertThat(saved.getEmail()).isEqualTo(spec.email());
        assertThat(saved.getMajor()).isEqualTo(spec.major());
        assertThat(saved.getGender()).isEqualTo(spec.gender());
        assertThat(saved.getPronouns()).isEqualTo(spec.pronouns());

        // The redirect points at this student's own page, and that page loads
        String profileUrl = result.getResponse().getRedirectedUrl();
        assertThat(profileUrl).isEqualTo("/students/" + saved.getId());
        mockMvc.perform(get(profileUrl))
                .andExpect(status().isOk())
                .andExpect(view().name("student-profile"))
                .andExpect(content().string(containsString(spec.fullName())));
    }

    @Test
    @DisplayName("All five profiles can exist at the same time, each with its own page")
    void allFiveProfilesSaveTogether() throws Exception {
        for (TestData.StudentSpec spec : TestData.students()) {
            mockMvc.perform(submit(spec)).andExpect(status().is3xxRedirection());
        }

        assertThat(studentRepository.count()).isEqualTo(5);
        for (TestData.StudentSpec spec : TestData.students()) {
            Student saved = studentRepository.findByStudentNumber(spec.studentNumber())
                    .orElseThrow(() -> new AssertionError("Profile missing for " + spec));
            mockMvc.perform(get("/students/" + saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(spec.studentNumber())));
        }
    }

    // Builds the same POST the browser sends from studentForm.html.
    // Optional fields are submitted as an empty string when they're unset, which is what the
    // "-- Select --" option does, Spring turns that into null.
    private MockHttpServletRequestBuilder submit(TestData.StudentSpec spec) {
        return post("/studentForm")
                .param("studentNumber", spec.studentNumber())
                .param("firstName", spec.firstName())
                .param("lastName", spec.lastName())
                .param("email", spec.email())
                .param("major", spec.major())
                .param("gender", spec.gender() == null ? "" : spec.gender().name())
                .param("pronouns", spec.pronouns() == null ? "" : spec.pronouns().name())
                .param("password", spec.password());
    }
}
