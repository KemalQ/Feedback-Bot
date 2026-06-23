package com.feedbackbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminUser /*implements UserDetails*/{
    @Id
    private Long id;
    private String username;
    private String password;
}
