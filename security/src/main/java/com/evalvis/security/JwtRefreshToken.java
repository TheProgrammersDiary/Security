package com.evalvis.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public final class JwtRefreshToken {
    private static final Logger logger = LoggerFactory.getLogger(JwtRefreshToken.class);

    private final String token;
    private final SecretKey key;

    public static JwtRefreshToken create(String username, Authentication authentication, SecretKey key) {
        int expirationMs = 1000 * 60 * 60 * 24 * 14;
        return new JwtRefreshToken(
                Jwts
                        .builder()
                        .subject(((User) authentication.getPrincipal()).getUsername())
                        .claim("username", username)
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + expirationMs))
                        .signWith(key)
                        .compact(),
                key
        );
    }

    public static Optional<JwtRefreshToken> existing(HttpServletRequest request, SecretKey key) {
        return parseJwt(request).map(token -> new JwtRefreshToken(token, key));
    }

    private JwtRefreshToken(String token, SecretKey key) {
        this.token = token;
        this.key = key;
    }

    private static Optional<String> parseJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return Optional.empty();
        }
        return Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals("jwt"))
                .findFirst()
                .map(Cookie::getValue);
    }

    public String value() {
        return token;
    }

    public String email() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String username() {
        return (String) Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("username");
    }

    public Date expirationDate() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public boolean tokenIsValid() {
        try {
            Jwts.parser().verifyWith(key).build().parse(token);
            return true;
        } catch(ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            logger.error("Failed to validate JWT token: {}", e.getMessage());
            return false;
        }
    }
}