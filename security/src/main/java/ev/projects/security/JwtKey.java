package ev.projects.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtKey {
    @Value("${jwt512}")
    private String plainKey;

    @Bean
    SecretKey value() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(plainKey));
    }
}
