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

    public List<Profile> getAllProfiles() {

        return profileRepository.findAll();
    }

    public Profile getProfileById(Long id) {

        return profileRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profile not found"
                        )
                );
    }

    public Profile getProfileByUser(User user) {

        return profileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Profile not found for this user"
                        )
                );
    }

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

    @Transactional
    public Profile saveProfileForUser(
            User user,
            Profile formProfile) {

        Profile profile =
                profileRepository
                        .findByUser(user)
                        .orElseGet(Profile::new);

        profile.setUser(user);

        copyProfileData(
                formProfile,
                profile
        );

        return profileRepository.save(profile);
    }

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

    public void deleteProfile(Long id) {

        Profile profile =
                getProfileById(id);

        profileRepository.delete(profile);
    }

    private void copyProfileData(
            Profile source,
            Profile target) {

        target.setFirstName(
                source.getFirstName());

        target.setLastName(
                source.getLastName());

        target.setCountry(
                source.getCountry());

        target.setCity(
                source.getCity());

        target.setTargetCity(
                source.getTargetCity());

        target.setTargetJob(
                source.getTargetJob());

        target.setExperienceYears(
                source.getExperienceYears());

        target.setSalaryExpectation(
                source.getSalaryExpectation());

        target.setLinkedinUrl(
                source.getLinkedinUrl());

        target.setGithubUrl(
                source.getGithubUrl());

        target.setPortfolioUrl(
                source.getPortfolioUrl());

        target.setPhone(
                source.getPhone());

        target.setNationality(
                source.getNationality());

        target.setAboutMe(
                source.getAboutMe());

        target.setGermanLevel(
                source.getGermanLevel());

        target.setEnglishLevel(
                source.getEnglishLevel());
    }
}