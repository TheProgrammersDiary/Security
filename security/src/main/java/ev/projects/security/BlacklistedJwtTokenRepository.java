package ev.projects.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BlacklistedJwtTokenRepository {
    private final Map<String, Date> blacklistedJwtTokenWithExpirationDate = new HashMap<>();

    public void blacklistToken(JwtToken token) {
        blacklistedJwtTokenWithExpirationDate.put(token.value(), token.expirationDate());
    }

    public boolean isTokenBlacklisted(String jwt) {
        return blacklistedJwtTokenWithExpirationDate.containsKey(jwt);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpiredTokens() {
        blacklistedJwtTokenWithExpirationDate
                .entrySet()
                .removeIf(tokenWithDate -> tokenWithDate.getValue().before(new Date()));
    }
}

