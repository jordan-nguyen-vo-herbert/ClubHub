package com.clubhub.controller;
import com.clubhub.dto.StudentResponse; // need to figure this out
import com.clubhub.repository.StudentRepository;
import com.clubhub.entities.Student; // need to figure this out

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;



@Controller // Tells Spring that this handles student-related requests and returns Thymeleaf pages
public class StudentController {
    private StudentRepository studentRepository;
    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students/{id}"
    public String studentProfile(@PathVariable Long id, Model model) { // uses id of type long to identify the student
        // No match -> HTTP 404, Spring shows templates/error/404.html
        Student match = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        // have a match, copy the safe fields into the DTO (no password)
        StudentResponse student = new StudentResponse(match.getStudentID(), match.getFirstName(), match.getLastName(),
                match.getEmail(), match.getMajor(), match.getGender(), match.getPronouns());
        model.addAttribute("student", student); // available in the template as ${student}
        return "student-profile"; // renders templates/student-profile.html
    }
}
