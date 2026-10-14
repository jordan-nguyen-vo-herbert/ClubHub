package com.clubhub.student;

import com.clubhub.repository.StudentRepository;
import com.clubhub.testdata.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// TEST-003 (FR-001): reusing a WSU ID shows "A profile already exists for this WSU ID"
// and keeps what the student typed, instead of crashing on the unique constraint.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("TEST-003: Duplicate WSU ID error (FR-001)")
class DuplicateStudentNumberTests {

    private static final String EXPECTED_MESSAGE = "A profile already exists for this WSU ID";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void seedExistingProfile() {
        // Ava already has a profile, so her WSU ID is taken
        studentRepository.save(TestData.AVA.toEntity());
    }

    @Test
    @DisplayName("Submitting a WSU ID that is already taken shows the error on the WSU ID field")
    void duplicateStudentNumberShowsFieldError() throws Exception {
        mockMvc.perform(post("/studentForm")
                        .param("studentNumber", TestData.AVA.studentNumber()) // already in use
                        .param("firstName", "Dana")
                        .param("lastName", "Whitmore")
                        .param("email", "dana.whitmore@wsu.edu")
                        .param("major", "Nursing")
                        .param("gender", "")
                        .param("pronouns", "")
                        .param("password", "DanaPass!23"))
                // No redirect, the form comes back so the student can fix it
                .andExpect(status().isOk())
                .andExpect(view().name("studentForm"))
                .andExpect(model().attributeHasFieldErrors("studentForm", "studentNumber"))
                .andExpect(result -> {
                    BindingResult binding = (BindingResult) result.getModelAndView()
                            .getModel().get("org.springframework.validation.BindingResult.studentForm");
                    assertThat(binding.getFieldError("studentNumber").getDefaultMessage())
                            .isEqualTo(EXPECTED_MESSAGE);
                })
                // The message is actually rendered, not just sitting in the model
                .andExpect(content().string(containsString(EXPECTED_MESSAGE)));
    }

    @Test
    @DisplayName("The rejected form still contains what the student typed")
    void duplicateStudentNumberKeepsWhatWasTyped() throws Exception {
        mockMvc.perform(post("/studentForm")
                        .param("studentNumber", TestData.AVA.studentNumber())
                        .param("firstName", "Dana")
                        .param("lastName", "Whitmore")
                        .param("email", "dana.whitmore@wsu.edu")
                        .param("major", "Nursing")
                        .param("gender", "WOMAN")
                        .param("pronouns", "SHE_HER")
                        .param("password", "DanaPass!23"))
                .andExpect(status().isOk())
                // Every visible field is pre-filled again, nobody has to retype the whole form
                .andExpect(content().string(containsString("value=\"" + TestData.AVA.studentNumber() + "\"")))
                .andExpect(content().string(containsString("value=\"Dana\"")))
                .andExpect(content().string(containsString("value=\"Whitmore\"")))
                .andExpect(content().string(containsString("value=\"dana.whitmore@wsu.edu\"")))
                .andExpect(content().string(containsString("value=\"Nursing\"")))
                // The dropdowns keep their selection too
                .andExpect(content().string(containsString("<option value=\"WOMAN\" selected=\"selected\"")))
                .andExpect(content().string(containsString("<option value=\"SHE_HER\" selected=\"selected\"")))
                // Thymeleaf never writes a value back into a password input, so the password is not echoed.
                // The field is required, so the student does have to retype just the password.
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("DanaPass!23"))));
    }

    @Test
    @DisplayName("The duplicate submission does not create a second profile")
    void duplicateStudentNumberIsNotSaved() throws Exception {
        long before = studentRepository.count();

        mockMvc.perform(post("/studentForm")
                        .param("studentNumber", TestData.AVA.studentNumber())
                        .param("firstName", "Dana")
                        .param("lastName", "Whitmore")
                        .param("email", "dana.whitmore@wsu.edu")
                        .param("major", "Nursing")
                        .param("gender", "")
                        .param("pronouns", "")
                        .param("password", "DanaPass!23"))
                .andExpect(status().isOk());

        assertThat(studentRepository.count()).isEqualTo(before);
        // The WSU ID still belongs to Ava, the second submission did not overwrite her
        assertThat(studentRepository.findByStudentNumber(TestData.AVA.studentNumber()))
                .get()
                .extracting(student -> student.getFirstName())
                .isEqualTo("Ava");
    }

    @Test
    @DisplayName("A different WSU ID with the same details is accepted")
    void differentStudentNumberStillSaves() throws Exception {
        // Guards against over-correcting: only the WSU ID is unique, not the name or email
        mockMvc.perform(post("/studentForm")
                        .param("studentNumber", "011223355") // one digit different from Ava's
                        .param("firstName", TestData.AVA.firstName())
                        .param("lastName", TestData.AVA.lastName())
                        .param("email", TestData.AVA.email())
                        .param("major", TestData.AVA.major())
                        .param("gender", "")
                        .param("pronouns", "")
                        .param("password", "AvaPass!24"))
                .andExpect(status().is3xxRedirection());

        assertThat(studentRepository.findByStudentNumber("011223355")).isPresent();
    }
}
