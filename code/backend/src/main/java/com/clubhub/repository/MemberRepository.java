package com.clubhub.repository;

import com.clubhub.entities.Member;
import com.clubhub.entities.Club;
import com.clubhub.entities.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
    // Find a student's membership in a particular club
    Optional<Member> findByStudentAndClub(Student student, Club club);
    // Retrieve all members of a particular club
    List<Member> findByClub(Club club);
}