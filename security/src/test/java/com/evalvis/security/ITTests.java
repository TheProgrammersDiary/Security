package com.evalvis.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ITTestsConfig.class)
@ActiveProfiles("it")
public class ITTests {
    @Autowired
    private JwtKey key;
    @Autowired
    private JwtFilter jwtFilter;

    @Test
    void authenticates() {
        JwtShortLivedToken token = JwtShortLivedToken.create(
                JwtRefreshToken.create(new FakeAuthentication("tester1@gmail.com", null), key.value()), key.value()
        );

        login(token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("tester1@gmail.com", ((User) authentication.getPrincipal()).getUsername());
        assertTrue(authentication.isAuthenticated());
    }

    private void login(JwtShortLivedToken token) {
        try {
            jwtFilter.doFilterInternal(
                    new FakeHttpServletRequest(
                            Map.of("Authorization", "Bearer " + token.value())
                    ),
                    new FakeHttpServletResponse(),
                    new FakeFilterChain()
            );
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
