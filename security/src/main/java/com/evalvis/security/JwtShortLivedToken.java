package com.evalvis.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

public final class JwtShortLivedToken {
    private static final Logger logger = LoggerFactory.getLogger(JwtShortLivedToken.class);

    private final String token;
    private final SecretKey key;

    public static JwtShortLivedToken create(JwtRefreshToken refreshToken, SecretKey key) {
        int expirationMs = 1000 * 60 * 10;
        return new JwtShortLivedToken(
                Jwts
                        .builder()
                        .subject(refreshToken.email())
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + expirationMs))
                        .signWith(key)
                        .compact(),
                key
        );
    }

    public static Optional<JwtShortLivedToken> existing(HttpServletRequest request, SecretKey key) {
        return parseJwt(request).map(token -> new JwtShortLivedToken(token, key));
    }

    private JwtShortLivedToken(String token, SecretKey key) {
        this.token = token;
        this.key = key;
    }

    private static Optional<String> parseJwt(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader("Authorization"))
                .flatMap(JwtShortLivedToken::parseJwtFromHeader);
    }

    private static Optional<String> parseJwtFromHeader(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }

    public String value() {
        return token;
    }

    public String email() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Date expirationDate() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public boolean tokenIsValid() {
        try {
            Jwts.parser().verifyWith(key).build().parse(token);
            return true;
        } catch(ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            logger.error("Exception while trying to validate JWT token: {}", e.getMessage());
            return false;
        }
    }
}