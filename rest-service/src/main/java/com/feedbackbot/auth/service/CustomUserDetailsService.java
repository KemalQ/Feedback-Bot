package com.feedbackbot.auth.service;

import com.feedbackbot.auth.security.CustomUserDetails;
import com.feedbackbot.auth.entity.AdminUser;
import com.feedbackbot.auth.dao.AdminUserDAO;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AdminUserDAO adminUserDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        AdminUser admin = adminUserDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
        return new CustomUserDetails(admin);
    }
}