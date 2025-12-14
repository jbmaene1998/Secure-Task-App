package com.securetaskapp.security;

import com.securetaskapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long expirationSeconds;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expirationSeconds}") long expirationSeconds,
            @Value("${app.jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationSeconds * 1000L);

        // IMPORTANT: subject must be a UUID string if you parse it as UUID later
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("roles", "ROLE_USER") // or a List<String> if you prefer
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims.getIssuer() == null || !issuer.equals(claims.getIssuer())) {
                return false;
            }

            UUID.fromString(claims.getSubject());

            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String getRolesFromToken(String token) {
        Object roles = parseClaims(token).get("roles");
        return roles != null ? roles.toString() : "";
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
