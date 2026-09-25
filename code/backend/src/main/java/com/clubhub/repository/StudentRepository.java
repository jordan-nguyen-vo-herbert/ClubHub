package com.clubhub.repository;
import com.clubhub.entities.Student;
import java.util.Optional;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
    // Specific 
    Optional<Student> findById(Long id); // inherited from CrudRepository, returns nothing if student not found
    // Student findByEmail(String email);
    // Many results
    List<Student> findByLastName(String lastName);
}
