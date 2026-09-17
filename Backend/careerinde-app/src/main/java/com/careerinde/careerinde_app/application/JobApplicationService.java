package com.careerinde.careerinde_app.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerinde.careerinde_app.user.User;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(
            JobApplicationRepository repository) {

        this.repository = repository;
    }


    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<JobApplication> getApplications(User user) {

        return repository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // =========================================================
    // GET ONE - OWNERSHIP PROTECTED
    // =========================================================

    @Transactional(readOnly = true)
    public JobApplication getApplication(
            Long applicationId,
            User user) {

        return repository
                .findByIdAndUser(applicationId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found"));
    }


    // =========================================================
    // CREATE
    // =========================================================

    public JobApplication createApplication(
            JobApplication application,
            User user) {

        application.setId(null);

        application.setUser(user);

        if (application.getStatus() == null) {
            application.setStatus(
                    ApplicationStatus.SAVED);
        }

        if (application.getApplicationDate() == null
                && application.getStatus()
                == ApplicationStatus.APPLIED) {

            application.setApplicationDate(
                    LocalDate.now());
        }

        return repository.save(application);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public JobApplication updateApplication(
            Long applicationId,
            JobApplication form,
            User user) {

        JobApplication existing =
                getApplication(
                        applicationId,
                        user);

        existing.setCompany(
                form.getCompany());

        existing.setPosition(
                form.getPosition());

        existing.setLocation(
                form.getLocation());

        existing.setJobUrl(
                form.getJobUrl());

        existing.setEmploymentType(
                form.getEmploymentType());

        existing.setSalaryRange(
                form.getSalaryRange());

        existing.setJobDescription(
                form.getJobDescription());

        existing.setSource(
                form.getSource());

        existing.setPriority(
                form.getPriority());

        existing.setMatchScore(
                form.getMatchScore());

        existing.setNotes(
                form.getNotes());

        existing.setApplicationDate(
                form.getApplicationDate());

        if (form.getStatus() != null) {
            existing.setStatus(
                    form.getStatus());
        }

        return repository.save(existing);
    }


    // =========================================================
    // STATUS
    // =========================================================

    public JobApplication changeStatus(
            Long applicationId,
            ApplicationStatus status,
            User user) {

        JobApplication application =
                getApplication(
                        applicationId,
                        user);

        application.setStatus(status);

        if (status == ApplicationStatus.APPLIED
                && application.getApplicationDate() == null) {

            application.setApplicationDate(
                    LocalDate.now());
        }

        return repository.save(application);
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void deleteApplication(
            Long applicationId,
            User user) {

        JobApplication application =
                getApplication(
                        applicationId,
                        user);

        repository.delete(application);
    }


    // =========================================================
    // DASHBOARD METRICS
    // =========================================================

    @Transactional(readOnly = true)
    public long countAll(User user) {

        return repository.countByUser(user);
    }


    @Transactional(readOnly = true)
    public long countByStatus(
            User user,
            ApplicationStatus status) {

        return repository
                .countByUserAndStatus(
                        user,
                        status);
    }
}