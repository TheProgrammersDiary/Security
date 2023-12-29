package com.evalvis.security;

public interface BlacklistedJwtTokenRepository {
    void blacklistToken(JwtToken token);
    boolean isTokenBlacklisted(String jwt);
    void removeExpiredTokens();
}
