package com.controller; // need to figure this out

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController // Tells Spring that this is handles API endpoint for student-related requests
public class StudentController {

    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students"
    public StudentResponse getStudentResponse(@Pathvariable Long id) { // uses id of type long to identify the student 
        
        // Need to be able to find and identify student from respository
        Student querey = StudentRepository.findByID(id);
        return new StudentResponse(querey.getID(), querey.getName(), querey.getEmail());
    }
}