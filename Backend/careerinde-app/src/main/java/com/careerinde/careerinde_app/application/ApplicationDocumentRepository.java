package com.careerinde.careerinde_app.application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerinde.careerinde_app.user.User;

public interface ApplicationDocumentRepository
        extends JpaRepository<ApplicationDocument, Long> {

    List<ApplicationDocument>
            findByApplicationAndApplicationUserOrderByUploadedAtDesc(
                    JobApplication application,
                    User user
            );

    Optional<ApplicationDocument>
            findByIdAndApplicationUser(
                    Long id,
                    User user
            );

    long countByApplicationAndApplicationUser(
            JobApplication application,
            User user
    );

    List<ApplicationDocument>
            findByApplicationAndApplicationUser(
                    JobApplication application,
                    User user
            );
}