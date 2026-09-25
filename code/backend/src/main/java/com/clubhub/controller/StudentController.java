package com.clubhub.controller;
import com.clubhub.dto.StudentResponse; // need to figure this out
import com.clubhub.repository.StudentRepository;
import com.clubhub.entities.Student; // need to figure this out


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;



@RestController // Tells Spring that this is handles API endpoint for student-related requests
public class StudentController {
    private StudentRepository studentRepository;
    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students"
    public StudentResponse getStudentResponse(@PathVariable Long id) { // uses id of type long to identify the student 
        
        // Need to be able to find and identify student from respository
        Student query = studentRepository.findById(id);
        return new StudentResponse(query.getStudentID(), query.getName(), query.getEmail());
    }
}