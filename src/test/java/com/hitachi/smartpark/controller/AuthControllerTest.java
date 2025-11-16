package com.hitachi.smartpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.smartpark.dto.AuthRequest;
import com.hitachi.smartpark.security.CustomUserDetailsService;
import com.hitachi.smartpark.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        AuthRequest request = new AuthRequest("admin", "admin123");
        String token = "test.jwt.token";

        when(userDetailsService.validateCredentials("admin", "admin123")).thenReturn(true);
        when(jwtUtil.generateToken("admin")).thenReturn(token);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("Should return 401 with invalid credentials")
    void shouldReturn401WithInvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest("admin", "wrongpassword");

        when(userDetailsService.validateCredentials("admin", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 when username is blank")
    void shouldReturn400WhenUsernameIsBlank() throws Exception {
        AuthRequest request = new AuthRequest("", "admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when password is blank")
    void shouldReturn400WhenPasswordIsBlank() throws Exception {
        AuthRequest request = new AuthRequest("admin", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

