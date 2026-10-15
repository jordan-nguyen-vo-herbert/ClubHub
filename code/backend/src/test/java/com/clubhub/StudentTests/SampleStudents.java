
package com.clubhub.StudentTests;

import java.util.List;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Provides sample student data and builds requests for student form tests.
 */
public class SampleStudents {

    /**
     * Stores the answers submitted through the student form.
     *
     * @param studentNumber the student's WSU ID
     * @param firstName     the student's first name
     * @param lastName      the student's last name
     * @param email         the student's email address
     * @param major         the student's major
     * @param gender        the selected gender, using the enum name
     * @param pronouns      the selected pronouns, using the enum name
     * @param password      the password entered in the form
     */
    public record StudentData(
            String studentNumber,
            String firstName,
            String lastName,
            String email,
            String major,
            String gender,
            String pronouns,
            String password) {
    }

    /**
     * Sample students used across the student form tests.
     * Claude helped generate the following Student Data Names and IDs.
     *
     * <p>
     * The data covers completed forms, blank optional fields,
     * different gender and pronoun selections, and names and IDs
     * that contain special formatting.
     * </p>
     */
    public static final List<StudentData> ALL = List.of(
            // All fields filled in
            new StudentData(
                    "011111111", "Butch", "Cougar",
                    "butch.cougar@wsu.edu", "Computer Science",
                    "MAN", "HE_HIM", "GoCougs1!"),

            // Different gender and pronouns
            new StudentData(
                    "022222222", "Priya", "Patel",
                    "priya.patel@wsu.edu", "Mathematics",
                    "WOMAN", "SHE_HER", "Pass2word"),

            // Gender and pronouns left blank
            new StudentData(
                    "033333333", "Alex", "Rivera",
                    "alex.rivera@wsu.edu", "Business",
                    "", "", "Secret#3"),

            // Major left blank
            new StudentData(
                    "044444444", "Sam", "Lee",
                    "sam.lee@wsu.edu", "",
                    "NONBINARY", "THEY_THEM", "hunter4"),

            // Hyphenated names and an ID with leading zeros
            new StudentData(
                    "000012345", "Mary-Jane", "Nguyen-Tran",
                    "mj.nguyentran@wsu.edu", "Mechanical Engineering",
                    "PREFER_NOT_TO_SAY", "SHE_THEY", "Cougs2026"));

    /**
     * Builds a POST request using the supplied student's form answers.
     *
     * @param student the student whose answers should be submitted
     * @return a request builder ready to use with MockMvc
     */
    public static MockHttpServletRequestBuilder submit(StudentData student) {
        return post("/studentForm")
                .param("studentNumber", student.studentNumber())
                .param("firstName", student.firstName())
                .param("lastName", student.lastName())
                .param("email", student.email())
                .param("major", student.major())
                .param("gender", student.gender())
                .param("pronouns", student.pronouns())
                .param("password", student.password());
    }
}
