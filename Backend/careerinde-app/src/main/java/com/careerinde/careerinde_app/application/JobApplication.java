package com.careerinde.careerinde_app.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.careerinde.careerinde_app.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_application")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // JOB INFORMATION
    // =========================================================

    @Column(nullable = false, length = 150)
    private String company;

    @Column(nullable = false, length = 150)
    private String position;

    @Column(length = 150)
    private String location;

    @Column(length = 1000)
    private String jobUrl;

    @Column(length = 100)
    private String employmentType;

    @Column(length = 100)
    private String salaryRange;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;


    // =========================================================
    // APPLICATION INFORMATION
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.SAVED;

    private LocalDate applicationDate;

    @Column(length = 100)
    private String source;

    private Integer matchScore;

    @Column(length = 30)
    private String priority;


    // =========================================================
    // NOTES
    // =========================================================

    @Column(columnDefinition = "TEXT")
    private String notes;


    // =========================================================
    // OWNER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // =========================================================
    // TIMESTAMPS
    // =========================================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public JobApplication() {
    }


    // =========================================================
    // JPA LIFECYCLE
    // =========================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = ApplicationStatus.SAVED;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }


    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }


    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }


    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }


    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }


    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {

        if (matchScore == null) {
            this.matchScore = null;
            return;
        }

        this.matchScore =
                Math.max(0, Math.min(100, matchScore));
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}