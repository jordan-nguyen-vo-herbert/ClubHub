package com.clubhub.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MembershipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    private Date requestDate = new Date();
    protected MembershipRequest() {}
    public MembershipRequest(Student student, Club club) {
        this.student = student;
        this.club = club;
    }
    public void accept() {
        this.status = RequestStatus.ACCEPTED;
    }
    public void reject() {
        this.status = RequestStatus.REJECTED;
    }
    public Long getId() {
        return this.id;
    }
    public Student getStudent() {
        return this.student;
    }
    public Club getClub() {
        return this.club;
    }
    public RequestStatus getStatus() {
        return this.status;
    }
    public Date getRequestDate() {
        return this.requestDate;
    }
}