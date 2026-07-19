package com.careerinde.careerinde_app.profile;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile updateProfile(Long id, Profile updatedProfile) {

        Profile existingProfile = getProfileById(id);

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setCountry(updatedProfile.getCountry());
        existingProfile.setCity(updatedProfile.getCity());
        existingProfile.setTargetCity(updatedProfile.getTargetCity());
        existingProfile.setTargetJob(updatedProfile.getTargetJob());
        existingProfile.setExperienceYears(updatedProfile.getExperienceYears());
        existingProfile.setSalaryExpectation(updatedProfile.getSalaryExpectation());
        existingProfile.setLinkedinUrl(updatedProfile.getLinkedinUrl());
        existingProfile.setGithubUrl(updatedProfile.getGithubUrl());
        existingProfile.setPortfolioUrl(updatedProfile.getPortfolioUrl());
        existingProfile.setPhone(updatedProfile.getPhone());
        existingProfile.setNationality(updatedProfile.getNationality());
        existingProfile.setAboutMe(updatedProfile.getAboutMe());
        existingProfile.setGermanLevel(updatedProfile.getGermanLevel());
        existingProfile.setEnglishLevel(updatedProfile.getEnglishLevel());

        return profileRepository.save(existingProfile);
    }

    public void deleteProfile(Long id) {
        Profile profile = getProfileById(id);
        profileRepository.delete(profile);
    }
}