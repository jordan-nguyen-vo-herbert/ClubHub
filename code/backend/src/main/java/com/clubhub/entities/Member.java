// A Member links one Student to one Club with a role, a student can have many Members (one per club)
package com.clubhub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// A student can only join the same club once
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "club_id"}))
public class Member {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id; // the membership's own ID, not the student's
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    // EnumType.STRING stores the name ("PRESIDENT"), not the position, so reordering the enum is safe
    @Enumerated(EnumType.STRING)
    private ClubRole role;
    // Might need status

    // Usage of default constructor  Java persistence API (JPA)
    protected Member() {}

    public Member(Student student, Club club, ClubRole role) {
        this.student = student;
        this.club = club;
        this.role = role;
    }

    public Long getMemberID() {
        return this.id;
    }

    public Student getStudent() {
        return this.student;
    }

    public Club getClub() {
        return this.club;
    }

    public ClubRole getRole() {
        return this.role;
    }
}
