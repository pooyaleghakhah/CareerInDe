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

    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String phone;

    private String nationality;

    // =========================================================
    // CAREER INFORMATION
    // =========================================================

    private String targetJob;

    private String targetCity;

    private Integer experienceYears;

    /*
     * Examples:
     * STUDENT
     * ENTRY_LEVEL
     * JUNIOR
     * MID
     * SENIOR
     */
    private String experienceLevel;

    private Double salaryExpectation;

    // =========================================================
    // JOB PREFERENCES
    // =========================================================

    /*
     * Examples:
     * REMOTE
     * HYBRID
     * ONSITE
     * FLEXIBLE
     */
    private String preferredWorkMode;

    private Boolean willingToRelocate;

    // =========================================================
    // SKILLS
    // =========================================================

    /*
     * Temporary MVP format:
     *
     * Java, Spring Boot, PostgreSQL, Docker, Git
     *
     * Later we can move skills into their own table.
     */
    @Column(length = 3000)
    private String skills;

    // =========================================================
    // LANGUAGES
    // =========================================================

    /*
     * Examples:
     * A1, A2, B1, B2, C1, C2, NATIVE
     */
    private String germanLevel;

    private String englishLevel;

    // =========================================================
    // PROFESSIONAL LINKS
    // =========================================================

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;

    // =========================================================
    // ABOUT
    // =========================================================

    @Column(length = 2000)
    private String aboutMe;

    // =========================================================
    // TIMESTAMPS
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================================================
    // USER RELATIONSHIP
    // =========================================================

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Profile() {
    }

    // =========================================================
    // ENTITY LIFECYCLE
    // =========================================================

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();
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

    public String getTargetJob() {
        return targetJob;
    }

    public void setTargetJob(String targetJob) {
        this.targetJob = targetJob;
    }

    public String getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(String targetCity) {
        this.targetCity = targetCity;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Double getSalaryExpectation() {
        return salaryExpectation;
    }

    public void setSalaryExpectation(Double salaryExpectation) {
        this.salaryExpectation = salaryExpectation;
    }

    public String getPreferredWorkMode() {
        return preferredWorkMode;
    }

    public void setPreferredWorkMode(String preferredWorkMode) {
        this.preferredWorkMode = preferredWorkMode;
    }

    public Boolean getWillingToRelocate() {
        return willingToRelocate;
    }

    public void setWillingToRelocate(Boolean willingToRelocate) {
        this.willingToRelocate = willingToRelocate;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
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