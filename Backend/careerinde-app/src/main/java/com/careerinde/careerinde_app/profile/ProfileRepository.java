package com.careerinde.careerinde_app.profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.careerinde.careerinde_app.user.User;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

}