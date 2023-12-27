package ev.projects.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public final class JwtToken {
    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);
    private static final int jwtExpirationMs = 1000 * 60 * 10;

    private final String token;
    private final SecretKey key;
    private final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository;

    public static JwtToken create(
            Authentication authentication, SecretKey key, BlacklistedJwtTokenRepository blacklistedJwtTokenRepository
    ) {
        return new JwtToken(
                Jwts
                        .builder()
                        .subject(((User) authentication.getPrincipal()).getUsername())
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                        .signWith(key)
                        .compact(),
                key,
                blacklistedJwtTokenRepository
        );
    }

    public static Optional<JwtToken> existing(
            HttpServletRequest request, SecretKey key, BlacklistedJwtTokenRepository blacklistedJwtTokenRepository
    ) {
        return parseJwt(request).map(token -> new JwtToken(token, key, blacklistedJwtTokenRepository));
    }

    private JwtToken(String token, SecretKey key, BlacklistedJwtTokenRepository blacklistedJwtTokenRepository) {
        this.token = token;
        this.key = key;
        this.blacklistedJwtTokenRepository = blacklistedJwtTokenRepository;
    }

    private static Optional<String> parseJwt(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader("Authorization"))
                .map(JwtToken::parseJwtFromHeader)
                .orElse(parseJwtFromCookies(request.getCookies()));
    }

    private static Optional<String> parseJwtFromHeader(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }

    private static Optional<String> parseJwtFromCookies(Cookie[] cookies) {
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

    public String username() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Date expirationDate() {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public boolean tokenIsValid() {
        try {
            Jwts.parser().verifyWith(key).build().parse(token);
            return !blacklistedJwtTokenRepository.isTokenBlacklisted(token);
        } catch(ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            logger.error("Exception while trying to validate JWT token: {}", e.getMessage());
            return false;
        }
    }
}