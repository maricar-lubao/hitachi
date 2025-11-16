package com.hitachi.smartpark.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${app.username:admin}")
    private String appUsername;

    @Value("${app.password:admin123}")
    private String appPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (appUsername.equals(username)) {
            return new User(appUsername, passwordEncoder.encode(appPassword), new ArrayList<>());
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    public boolean validateCredentials(String username, String password) {
        return appUsername.equals(username) && appPassword.equals(password);
    }
}

