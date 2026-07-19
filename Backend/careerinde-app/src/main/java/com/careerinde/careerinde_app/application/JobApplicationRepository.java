package com.careerinde.careerinde_app.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerinde.careerinde_app.user.User;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUser(User user);
}