package com.careerinde.careerinde_app.job.matching;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.careerinde.careerinde_app.profile.Profile;
import com.careerinde.careerinde_app.profile.ProfileService;
import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class RecommendedJobController {

    private final RecommendedJobService recommendedJobService;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    public RecommendedJobController(
            RecommendedJobService recommendedJobService,
            ProfileService profileService,
            UserRepository userRepository) {

        this.recommendedJobService =
                recommendedJobService;

        this.profileService =
                profileService;

        this.userRepository =
                userRepository;
    }


    @GetMapping("/recommended-jobs")
    public String showRecommendedJobs(
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {

            /*
             * 1. Find logged-in user
             */
            User user =
                    userRepository
                            .findByEmail(
                                    principal.getName()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found."
                                    )
                            );


            /*
             * 2. Load user's profile
             */
            Profile profile =
                    profileService
                            .getProfileByUser(user);


            /*
             * 3. Find personalized jobs
             *
             * Cohere will rerank the jobs
             * and return the best 10.
             */
            List<RecommendedJob> jobs =
                    recommendedJobService
                            .findRecommendedJobs(
                                    profile,
                                    10
                            );


            /*
             * 4. Send results to Thymeleaf
             */
            model.addAttribute(
                    "recommendedJobs",
                    jobs
            );

            model.addAttribute(
                    "profile",
                    profile
            );

            model.addAttribute(
                    "targetJob",
                    profile.getTargetJob()
            );

            model.addAttribute(
                    "targetCity",
                    profile.getTargetCity()
            );


            return "recommended-jobs";


        } catch (IllegalStateException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            return "recommended-jobs";


        } catch (Exception exception) {

            System.err.println(
                    "===== RECOMMENDED JOB ERROR ====="
            );

            exception.printStackTrace();

            System.err.println(
                    "================================="
            );

            model.addAttribute(
                    "error",
                    "Personalized job recommendations are temporarily unavailable."
            );

            return "recommended-jobs";
        }
    }
}