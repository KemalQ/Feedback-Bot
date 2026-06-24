package com.feedbackbot.repository;

import com.feedbackbot.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

}
