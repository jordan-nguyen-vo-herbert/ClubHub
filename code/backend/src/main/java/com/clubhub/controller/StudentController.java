package com.clubhub.controller;
import com.clubhub.dto.StudentResponse; // need to figure this out
import com.clubhub.repository.StudentRepository;
import com.clubhub.entities.Student; // need to figure this out
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;



@RestController // Tells Spring that this is handles API endpoint for student-related requests
public class StudentController {
    private StudentRepository studentRepository;
    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students"
    public StudentResponse getStudentResponse(@PathVariable Long id) { // uses id of type long to identify the student 
        Optional<Student> query = studentRepository.findById(id);
        if (!query.isPresent()) {
            // this will need to be fixed, either HTTP 404, or not found page
            query.orElseThrow(()-> new RuntimeException("Student not found")); 
        }
        // have a match, can return
        Student match = query.get();
        return new StudentResponse(match.getStudentID(), match.getName(), match.getEmail());
    }
}