package com.EMPMANAGE.Security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.EMPMANAGE.Entity.UserAuthentication;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

    private final Key key;
    private final long expireTime = 86_400_000L;

    public JWTUtil(@Value("${app.jwt.secret:}") String secret) {
        if (secret == null || secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be configured and contain at least 32 UTF-8 bytes");
        }
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String generateToken(UserAuthentication user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUserOfficialEmail())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireTime))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getUserEmail(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}
