package com.clubhub.repository;

import com.clubhub.entities.MembershipRequest;
import com.clubhub.entities.Club;
import com.clubhub.entities.Student;
import com.clubhub.entities.RequestStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MembershipRequestRepository
        extends CrudRepository<MembershipRequest, Long> {

    List<MembershipRequest> findByClubAndStatus(
        Club club, RequestStatus status);

    Optional<MembershipRequest> findByStudentAndClubAndStatus(
        Student student, Club club, RequestStatus status);
}