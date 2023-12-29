package com.evalvis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@Service
public class BlacklistedJwtTokenRedisRepository implements BlacklistedJwtTokenRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

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
        Iterator<String> keys = redisTemplate.keys("jwtblacklist_*").iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                if(dateFormat.parse(redisTemplate.opsForValue().get(key)).before(new Date())) {
                    redisTemplate.delete(key);
                    keys.remove();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

