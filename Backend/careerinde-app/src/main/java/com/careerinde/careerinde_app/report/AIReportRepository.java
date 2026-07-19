package com.careerinde.careerinde_app.report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerinde.careerinde_app.user.User;

public interface AIReportRepository
        extends JpaRepository<AIReport, Long> {

    List<AIReport> findByUser(User user);
}