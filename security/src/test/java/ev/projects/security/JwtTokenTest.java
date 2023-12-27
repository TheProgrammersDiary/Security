package ev.projects.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenTest {
    private static final SecretKey key = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(
                    "b936cee86c9f87aa5d3c6f2e84cb5a4239a5fe50480a6ec66b70ab5b1f4ac6730c6c51542" +
                    "1b327ec1d69402e53dfb49ad7381eb067b338fd7b0cb22247225d47"
            )
    );

    private final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository = new BlacklistedJwtTokenRepository();

    @Test
    void createsToken() {
        JwtToken token = JwtToken.create(new FakeAuthentication("tester"), key, blacklistedJwtTokenRepository);

        assertAll(
                () -> assertEquals("tester", token.username()),
                () -> assertTrue(token.expirationDate().after(new Date(System.currentTimeMillis()))),
                () -> assertTrue(token.tokenIsValid())
        );
    }

    @Test
    void readsExistingTokenFromAuthorizationHeader() {
        HttpServletRequest httpRequest = new FakeHttpServletRequest(
                Map.of(
                        "Authorization",
                        "Bearer " +
                                JwtToken.create(
                                        new FakeAuthentication("tester"), key, blacklistedJwtTokenRepository
                                ).value()
                )
        );

        Optional<JwtToken> token = JwtToken.existing(httpRequest, key, blacklistedJwtTokenRepository);

        assertAll(
                () -> assertTrue(token.isPresent()),
                () -> assertEquals("tester", token.get().username()),
                () -> assertTrue(token.get().expirationDate().after(new Date(System.currentTimeMillis()))),
                () -> assertTrue(token.get().tokenIsValid())
        );
    }

    @Test
    void readsExistingTokenFromCookies() {
        HttpServletRequest httpRequest = new FakeHttpServletRequest(
                new Cookie[]{
                        new Cookie(
                                "jwt",
                                JwtToken.create(
                                        new FakeAuthentication("tester"), key, blacklistedJwtTokenRepository
                                ).value()
                        )
                }
        );

        Optional<JwtToken> token = JwtToken.existing(httpRequest, key, blacklistedJwtTokenRepository);

        assertAll(
                () -> assertTrue(token.isPresent()),
                () -> assertEquals("tester", token.get().username()),
                () -> assertTrue(token.get().expirationDate().after(new Date(System.currentTimeMillis()))),
                () -> assertTrue(token.get().tokenIsValid())
        );
    }
}
