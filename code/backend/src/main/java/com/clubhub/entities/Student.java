package main.java.com.clubhub.entities;

public class Student {
    private long id;
    private String name;
    private String email;
    private String password; // will probably need to make this a hashed password for security

    public Student(long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        // List of clubs the student is a member of
    }

    // Returns the student's ID
    public long getId() {
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

    // Returns the student's password for security reasons, this might be hashed)
    public String getPassword() {
        return password;
    }
}