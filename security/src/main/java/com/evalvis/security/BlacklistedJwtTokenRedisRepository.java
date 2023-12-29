package com.evalvis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BlacklistedJwtTokenRedisRepository implements BlacklistedJwtTokenRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void blacklistToken(JwtToken token) {
        redisTemplate.opsForValue().set("jwtblacklist_" + token.value(), token.expirationDate().toString());
    }

    @Override
    public boolean isTokenBlacklisted(String jwt) {
        return redisTemplate.hasKey("jwtblacklist_" + jwt);
    }

    @Override
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpiredTokens() {
        redisTemplate
                .keys("jwtblacklist_*")
                .removeIf(
                        date -> {
                            try {
                                return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                                        .parse(date)
                                        .before(new Date());
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }
}

