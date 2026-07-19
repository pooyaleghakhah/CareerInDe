package com.careerinde.careerinde_app.profile;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public Profile createProfile(@RequestBody Profile profile) {
        return profileService.createProfile(profile);
    }

    @GetMapping
    public List<Profile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @GetMapping("/{id}")
    public Profile getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

    @PutMapping("/{id}")
    public Profile updateProfile(
            @PathVariable Long id,
            @RequestBody Profile profile) {

        return profileService.updateProfile(id, profile);
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
    }
}