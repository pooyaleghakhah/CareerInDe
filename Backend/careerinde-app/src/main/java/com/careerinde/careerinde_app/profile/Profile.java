package com.careerinde.careerinde_app.profile;

import com.careerinde.careerinde_app.user.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String targetCity;

    private String targetJob;

    private Integer experienceYears;

    private Double salaryExpectation;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String phone;

    private String nationality;

    @Column(length = 2000)
    private String aboutMe;

    private String germanLevel;

    private String englishLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Profile() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(String targetCity) {
        this.targetCity = targetCity;
    }

    public String getTargetJob() {
        return targetJob;
    }

    public void setTargetJob(String targetJob) {
        this.targetJob = targetJob;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Double getSalaryExpectation() {
        return salaryExpectation;
    }

    public void setSalaryExpectation(Double salaryExpectation) {
        this.salaryExpectation = salaryExpectation;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getGermanLevel() {
        return germanLevel;
    }

    public void setGermanLevel(String germanLevel) {
        this.germanLevel = germanLevel;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}