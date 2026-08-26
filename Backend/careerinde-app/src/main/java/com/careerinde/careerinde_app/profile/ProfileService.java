package com.careerinde.careerinde_app.profile;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerinde.careerinde_app.user.User;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(
            ProfileRepository profileRepository) {

        this.profileRepository = profileRepository;
    }

    // =========================================================
    // CREATE PROFILE
    // =========================================================

    public Profile createProfile(Profile profile) {

        if (profile.getUser() == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        if (profileRepository
                .existsByUser(profile.getUser())) {

            throw new RuntimeException(
                    "Profile already exists for this user"
            );
        }

        return profileRepository.save(profile);
    }

    // =========================================================
    // GET ALL PROFILES
    // =========================================================

    public List<Profile> getAllProfiles() {

        return profileRepository.findAll();
    }

    // =========================================================
    // GET PROFILE BY ID
    // =========================================================

    public Profile getProfileById(Long id) {

        return profileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profile not found"
                        )
                );
    }

    // =========================================================
    // GET PROFILE BY USER
    // =========================================================

    public Profile getProfileByUser(User user) {

        return profileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profile not found for this user"
                        )
                );
    }

    // =========================================================
    // GET EXISTING PROFILE OR CREATE EMPTY PROFILE
    // =========================================================

    public Profile getProfileOrEmpty(User user) {

        return profileRepository
                .findByUser(user)
                .orElseGet(() -> {

                    Profile profile =
                            new Profile();

                    profile.setUser(user);

                    return profile;
                });
    }

    // =========================================================
    // SAVE PROFILE FOR LOGGED-IN USER
    // =========================================================

    @Transactional
    public Profile saveProfileForUser(
            User user,
            Profile formProfile) {

        Profile profile =
                profileRepository
                        .findByUser(user)
                        .orElseGet(Profile::new);

        /*
         * Important:
         * We always assign the authenticated user here.
         *
         * We do NOT trust user_id coming from the HTML form.
         */
        profile.setUser(user);

        copyProfileData(
                formProfile,
                profile
        );

        return profileRepository.save(profile);
    }

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    @Transactional
    public Profile updateProfile(
            Long id,
            Profile updatedProfile) {

        Profile existingProfile =
                getProfileById(id);

        copyProfileData(
                updatedProfile,
                existingProfile
        );

        return profileRepository.save(
                existingProfile
        );
    }

    // =========================================================
    // DELETE PROFILE
    // =========================================================

    public void deleteProfile(Long id) {

        Profile profile =
                getProfileById(id);

        profileRepository.delete(profile);
    }

    // =========================================================
    // COPY PROFILE DATA
    // =========================================================

    private void copyProfileData(
            Profile source,
            Profile target) {

        // Personal information

        target.setFirstName(
                source.getFirstName());

        target.setLastName(
                source.getLastName());

        target.setCountry(
                source.getCountry());

        target.setCity(
                source.getCity());

        target.setPhone(
                source.getPhone());

        target.setNationality(
                source.getNationality());


        // Career information

        target.setTargetJob(
                source.getTargetJob());

        target.setTargetCity(
                source.getTargetCity());

        target.setExperienceYears(
                source.getExperienceYears());

        target.setExperienceLevel(
                source.getExperienceLevel());

        target.setSalaryExpectation(
                source.getSalaryExpectation());


        // Job preferences

        target.setPreferredWorkMode(
                source.getPreferredWorkMode());

        target.setWillingToRelocate(
                source.getWillingToRelocate());


        // Skills

        target.setSkills(
                source.getSkills());


        // Languages

        target.setGermanLevel(
                source.getGermanLevel());

        target.setEnglishLevel(
                source.getEnglishLevel());


        // Professional links

        target.setLinkedinUrl(
                source.getLinkedinUrl());

        target.setGithubUrl(
                source.getGithubUrl());

        target.setPortfolioUrl(
                source.getPortfolioUrl());


        // About

        target.setAboutMe(
                source.getAboutMe());
    }
}