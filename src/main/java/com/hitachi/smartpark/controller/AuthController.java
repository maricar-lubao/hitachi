package com.hitachi.smartpark.controller;

import com.hitachi.smartpark.dto.AuthRequest;
import com.hitachi.smartpark.dto.AuthResponse;
import com.hitachi.smartpark.security.CustomUserDetailsService;
import com.hitachi.smartpark.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        if (!userDetailsService.validateCredentials(request.getUsername(), request.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        AuthResponse response = new AuthResponse(token, request.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

