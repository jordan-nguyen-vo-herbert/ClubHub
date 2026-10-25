package com.clubhub.security;

import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Spring Security finds this bean and uses it to look students up when someone signs in
public class StudentUserDetailsService implements UserDetailsService {
    private final StudentRepository studentRepository;

    public StudentUserDetailsService(StudentRepository studentRepository) { // Spring passes in the repository automatically
        this.studentRepository = studentRepository;
    }

    // Called with whatever was typed in the "email" field of the sign-in form
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // No match -> UsernameNotFoundException, which Spring reports the same way as a wrong password,
        // so the sign-in page never reveals which emails have a profile (NFR-010-02)
        return studentRepository.findByEmail(Student.normalizeEmail(email))
                .map(StudentPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("No profile for this email"));
    }
}
