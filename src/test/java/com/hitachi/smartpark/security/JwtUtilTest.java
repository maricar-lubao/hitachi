package com.hitachi.smartpark.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JWT Util Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "SmartParkSecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        String token = jwtUtil.generateToken("admin");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        String token = jwtUtil.generateToken("admin");

        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateTokenSuccessfully() {
        String token = jwtUtil.generateToken("admin");

        Boolean isValid = jwtUtil.validateToken(token, "admin");

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject token with wrong username")
    void shouldRejectTokenWithWrongUsername() {
        String token = jwtUtil.generateToken("admin");

        Boolean isValid = jwtUtil.validateToken(token, "wronguser");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void shouldExtractExpirationDateFromToken() {
        String token = jwtUtil.generateToken("admin");

        var expiration = jwtUtil.extractExpiration(token);

        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new java.util.Date());
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        JwtUtil shortLivedJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortLivedJwtUtil, "secret", "SmartParkSecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough");
        ReflectionTestUtils.setField(shortLivedJwtUtil, "expiration", -1000L);

        String token = shortLivedJwtUtil.generateToken("admin");

        assertThatThrownBy(() -> shortLivedJwtUtil.extractUsername(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        assertThat(token1).isNotEqualTo(token2);

        String username1 = jwtUtil.extractUsername(token1);
        String username2 = jwtUtil.extractUsername(token2);

        assertThat(username1).isEqualTo("user1");
        assertThat(username2).isEqualTo("user2");
    }

    @Test
    @DisplayName("Should handle malformed token")
    void shouldHandleMalformedToken() {
        String malformedToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtUtil.extractUsername(malformedToken))
                .isInstanceOf(Exception.class);
    }
}

