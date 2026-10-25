package com.clubhub.security;

import java.util.regex.Pattern;

import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Runs once every time ClubHub starts: profiles saved before sign-in existed kept the typed password,
// so this replaces each one with its BCrypt hash (FR-010-02). Running it again changes nothing.
@Component
public class PasswordMigration implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(PasswordMigration.class);

    // A BCrypt hash looks like "$2a$10$" followed by 53 characters ($2b$ and $2y$ are newer spellings)
    private static final Pattern BCRYPT_HASH = Pattern.compile("\\$2[aby]\\$\\d{2}\\$.{53}");

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigration(StudentRepository studentRepository, PasswordEncoder passwordEncoder) { // Spring passes both in automatically
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        int hashed = 0;
        for (Student student : studentRepository.findAll()) {
            String stored = student.getPassword();
            if (stored != null && !BCRYPT_HASH.matcher(stored).matches()) {
                student.replacePasswordHash(passwordEncoder.encode(stored)); // the old typed password still works for sign-in
                studentRepository.save(student);
                hashed++;
            }
        }
        if (hashed > 0) {
            log.info("Hashed {} plain-text password(s) with BCrypt", hashed); // a count only, never a password (NFR-010-01)
        }
    }
}
