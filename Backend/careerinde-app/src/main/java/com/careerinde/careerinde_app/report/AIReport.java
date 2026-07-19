package com.careerinde.careerinde_app.report;

import java.time.LocalDateTime;

import com.careerinde.careerinde_app.user.User;

import jakarta.persistence.*;

@Entity
public class AIReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;

    @Column(columnDefinition = "TEXT")
    private String result;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    public AIReport() {

        this.createdAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}