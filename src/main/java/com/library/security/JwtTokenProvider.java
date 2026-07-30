package com.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - generates, validates, and extracts information from JWT tokens.
 * 
 * Features:
 * - Symmetric key signing (HS256)
 * - Configurable token expiration
 * - Automatic token validation
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token from Authentication object.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var key = new SecretKeySpec(keyBytes, 0, keyBytes.length, SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Generate JWT token from username (used for testing).
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var key = new SecretKeySpec(keyBytes, 0, keyBytes.length, SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Extract username from JWT token.
     */
    public String getUsernameFromJwt(String token) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var key = new SecretKeySpec(keyBytes, 0, keyBytes.length, SignatureAlgorithm.HS512.getJcaName());

        Claims claims = (Claims) Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }

    /**
     * Get expiration date from JWT token.
     */
    public Date getExpirationDateFromJwt(String token) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var key = new SecretKeySpec(keyBytes, 0, keyBytes.length, SignatureAlgorithm.HS512.getJcaName());

        Claims claims = (Claims) Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getExpiration();
    }

    /**
     * Validate JWT token.
     * Returns true if token is valid, false otherwise.
     */
    public boolean validateToken(String authToken) {
        try {
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            var key = new SecretKeySpec(keyBytes, 0, keyBytes.length, SignatureAlgorithm.HS512.getJcaName());

            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Extract token from Authorization header.
     * Expected format: "Bearer <token>"
     */
    public String getTokenFromBearerString(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return bearerToken;
    }

}
