package com.clubhub.controller;

import com.clubhub.dto.StudentForm;
import com.clubhub.dto.StudentResponse;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller // Tells Spring that this handles student-related requests and returns Thymeleaf pages
public class StudentController {
    private StudentRepository studentRepository;
    public StudentController(StudentRepository studentRepository) { // Spring passes in the repository automatically
        this.studentRepository = studentRepository;
    }

    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students/{id}"
    public String studentProfile(@PathVariable Long id, Model model) { // uses id of type long to identify the student
        // No match -> HTTP 404, Spring shows templates/error/404.html
        Student match = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        // have a match, copy the safe fields into the DTO (no password)
        StudentResponse student = new StudentResponse(match.getId(), match.getFirstName(), match.getLastName(),
                match.getEmail(), match.getMajor(), match.getGender(), match.getPronouns());

        model.addAttribute("student", student); // available in the template as ${student}
        return "student-profile"; // renders templates/student-profile.html
    }

    @GetMapping("/studentForm") // GET: user opens the create-profile page
    public String studentForm(Model model) { // Spring passes in an empty Model
        model.addAttribute("studentForm", new StudentForm()); // blank form object, the template's th:object="${studentForm}" binds to it
        return "studentForm"; // renders templates/studentForm.html (Yohann)
    }

    @PostMapping("/studentForm") // POST: user clicks submit on the form
    public String studentFormSubmit(@ModelAttribute StudentForm form) {
        // 1. Convert: form DTO -> entity (Student has no setters, so use the constructor)
        Student student = new Student(form.getStudentNumber(), form.getFirstName(), form.getLastName(),
                form.getEmail(), form.getMajor(), form.getGender(), form.getPronouns(), form.getPassword());

        // 2. Save: the database assigns the id, save() returns the stored copy with it filled in
        Student saved = studentRepository.save(student);

        // 3. Redirect: send the browser to the new profile page
        return "redirect:/students/" + saved.getId();
    }

}
