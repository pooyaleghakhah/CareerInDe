package com.careerinde.careerinde_app.application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerinde.careerinde_app.user.User;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    // =========================================================
    // EXISTING / BACKWARD COMPATIBILITY
    // =========================================================

    List<JobApplication> findByUser(User user);


    // =========================================================
    // USER APPLICATIONS
    // =========================================================

    List<JobApplication> findByUserOrderByCreatedAtDesc(User user);


    // =========================================================
    // SECURE OWNERSHIP LOOKUP
    // =========================================================

    Optional<JobApplication> findByIdAndUser(
            Long id,
            User user
    );


    // =========================================================
    // STATUS FILTER
    // =========================================================

    List<JobApplication> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            ApplicationStatus status
    );


    // =========================================================
    // DASHBOARD METRICS
    // =========================================================

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            ApplicationStatus status
    );


    // =========================================================
    // SECURE DELETE
    // =========================================================

    long deleteByIdAndUser(
            Long id,
            User user
    );
}