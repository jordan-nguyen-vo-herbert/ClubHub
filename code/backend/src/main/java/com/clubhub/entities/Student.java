package main.java.com.clubhub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    // will need enum for major
    private String password; 

    // Usage of default constructor  Java persistence API (JPA)
    protected Student() {}

    public Student(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        // List of clubs the student is a member of -> would need to update resource controller and record
    }

    // Returns the student's ID
    public Long getId() {
        return id;
    }

    // Returns the student's name
    public String getName() {
        return name;
    }

    // Returns the student's email address
    public String getEmail() {
        return email;
    }

    // Returns the student's password for security reasons, this might be hashed
    public String getPassword() {
        return password;
    }
}