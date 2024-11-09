package dev.ehyeon.auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${spring.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${spring.jwt.access-token-expiration-Millisecond}")
    private int accessTokenExpirationMillisecond;

    private Key key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(jwtSecretKey.getBytes(StandardCharsets.UTF_8)).getBytes());
    }

    public String generateAccessToken(long memberId) {
        return generateToken(memberId, accessTokenExpirationMillisecond);
    }

    private String generateToken(long memberId, int expirationTime) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(Long.toString(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getMemberIdFromToken(String token) {
        try {
            return Long.parseLong(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject());
        } catch (Exception exception) {
            throw new InvalidTokenException();
        }
    }
}
