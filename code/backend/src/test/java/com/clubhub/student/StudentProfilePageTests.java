package com.clubhub.student;

import com.clubhub.dto.StudentResponse;
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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// TEST-004 (FR-001): the profile page shows name, email, major, gender, pronouns and WSU ID,
// and never the password. The missing-id case lives in StudentProfileNotFoundTests, which needs
// a real servlet container to render the 404 page.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("TEST-004: Student profile page (FR-001)")
class StudentProfilePageTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;

    @ParameterizedTest(name = "{0}'s profile shows their details and no password")
    @MethodSource("com.clubhub.testdata.TestData#studentStream")
    void profileShowsDetailsAndHidesPassword(TestData.StudentSpec spec) throws Exception {
        Student saved = studentRepository.save(spec.toEntity());

        String page = mockMvc.perform(get("/students/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("student-profile"))
                .andExpect(content().string(containsString(spec.fullName())))
                .andExpect(content().string(containsString(spec.studentNumber())))
                .andExpect(content().string(containsString(spec.email())))
                .andExpect(content().string(containsString(spec.major())))
                // The password is never rendered, not even hashed or masked
                .andExpect(content().string(not(containsString(spec.password()))))
                .andReturn().getResponse().getContentAsString();

        // Gender and pronouns are optional: shown with their display text when set, hidden when not
        if (spec.gender() != null) {
            assertThat(page).contains(spec.gender().getGender());
        }
        if (spec.pronouns() != null) {
            assertThat(page).contains(spec.pronouns().getPronouns());
        }
    }

    @Test
    @DisplayName("A profile with no gender or pronouns still renders")
    void optionalFieldsCanBeMissing() throws Exception {
        // Jae has neither, the template's th:if branches have to handle null without failing
        Student saved = studentRepository.save(TestData.JAE.toEntity());

        mockMvc.perform(get("/students/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TestData.JAE.fullName())))
                .andExpect(content().string(containsString(TestData.JAE.studentNumber())));
    }

    @Test
    @DisplayName("The page model carries a StudentResponse, which has no password field")
    void modelUsesPasswordFreeDto() throws Exception {
        Student saved = studentRepository.save(TestData.AVA.toEntity());

        mockMvc.perform(get("/students/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(result -> {
                    Object student = result.getModelAndView().getModel().get("student");
                    // A Student entity in the model would hand the template a getPassword()
                    assertThat(student).isInstanceOf(StudentResponse.class);
                    StudentResponse response = (StudentResponse) student;
                    assertThat(response.studentNumber()).isEqualTo(TestData.AVA.studentNumber());
                    assertThat(response.email()).isEqualTo(TestData.AVA.email());
                    assertThat(StudentResponse.class.getRecordComponents())
                            .noneMatch(component -> component.getName().toLowerCase().contains("password"));
                });
    }
}
