package com.careerinde.careerinde_app.application.coverletter;

public class CoverLetter {

    private String jobTitle;
    private String companyName;
    private String subject;
    private String greeting;
    private String body;
    private String closing;
    private String candidateName;


    public CoverLetter() {
    }


    public String getJobTitle() {
        return jobTitle;
    }


    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }


    public String getCompanyName() {
        return companyName;
    }


    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getGreeting() {
        return greeting;
    }


    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }


    public String getClosing() {
        return closing;
    }


    public void setClosing(String closing) {
        this.closing = closing;
    }


    public String getCandidateName() {
        return candidateName;
    }


    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }


    @Override
    public String toString() {
        return "CoverLetter{" +
                "jobTitle='" + jobTitle + '\'' +
                ", companyName='" + companyName + '\'' +
                ", subject='" + subject + '\'' +
                ", greeting='" + greeting + '\'' +
                ", body='" + body + '\'' +
                ", closing='" + closing + '\'' +
                ", candidateName='" + candidateName + '\'' +
                '}';
    }
}