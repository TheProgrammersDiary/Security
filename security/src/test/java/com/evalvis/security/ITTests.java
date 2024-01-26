package com.evalvis.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRedisConfig.class)
@ActiveProfiles("it")
public class ITTests {
    @Autowired
    private JwtKey key;
    @Autowired
    private BlacklistedJwtTokenRedisRepository blacklistedJwtTokenRedisRepository;
    @Autowired
    private JwtFilter jwtFilter;

    @Test
    void authenticates() {
        JwtToken token = JwtToken.create(
                new FakeAuthentication("tester1", null), key.value(), blacklistedJwtTokenRedisRepository
        );

        login(token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("tester1", ((User) authentication.getPrincipal()).getUsername());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void blacklistsToken() {
        JwtToken token = JwtToken.create(
                new FakeAuthentication("tester2", null), key.value(), blacklistedJwtTokenRedisRepository
        );

        blacklistedJwtTokenRedisRepository.blacklistToken(token);

        assertTrue(blacklistedJwtTokenRedisRepository.isTokenBlacklisted(token.value()));
        login(token);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void login(JwtToken token) {
        try {
            jwtFilter.doFilterInternal(
                    new FakeHttpServletRequest(
                            Map.of("X-CSRF-TOKEN", token.csrfToken()),
                            new Cookie[]{new Cookie("jwt", token.value())}
                    ),
                    new FakeHttpServletResponse(),
                    new FakeFilterChain()
            );
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void doesNotRemoveValidBlacklistedTokens() {
        JwtToken token = JwtToken.create(
                new FakeAuthentication("tester", null), key.value(), blacklistedJwtTokenRedisRepository
        );
        blacklistedJwtTokenRedisRepository.blacklistToken(token);

        blacklistedJwtTokenRedisRepository.removeExpiredTokens();

        assertTrue(blacklistedJwtTokenRedisRepository.isTokenBlacklisted(token.value()));
    }
}
