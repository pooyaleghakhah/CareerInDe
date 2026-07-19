package com.careerinde.careerinde_app.application;

import java.time.LocalDate;

import com.careerinde.careerinde_app.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class JobApplication {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;

    private String position;

    private String status;

    private LocalDate applicationDate;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public JobApplication() {
    }

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

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public LocalDate getApplicationDate() {

        return applicationDate;
    }

    public void setApplicationDate(
            LocalDate applicationDate) {

        this.applicationDate =
                applicationDate;
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
}