package com.clubhub.repository;

import com.clubhub.entities.Feedback;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {
    // Every piece of feedback, newest first, for whoever reviews it (FR-008-02)
    // Spring writes the query from the method name, names must match Feedback's fields
    List<Feedback> findAllByOrderBySubmittedAtDesc();
}
