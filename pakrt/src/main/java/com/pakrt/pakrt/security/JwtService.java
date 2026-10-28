package com.pakrt.pakrt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret-key:Kunc1R4h4s1a}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms:600000}")
    private long jwtExpirationMs;

    /**
     * Menghasilkan HMAC-SHA256 SecretKey dengan Opsi B (Key Derivation via SHA-256).
     * Menjamin panjang kunci selalu 256-bit berstandar kriptografi teraman.
     */
    private SecretKey getSigningKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Menerbitkan Access Token Bearer dengan durasi tepat 10 menit (600.000 ms).
     */
    public String generateToken(String username, String role, String accountId) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (role != null) extraClaims.put("role", role);
        if (accountId != null) extraClaims.put("accountId", accountId);
        return buildToken(extraClaims, username, jwtExpirationMs);
    }

    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validasi token (memastikan signature cocok dan belum expired > 10 menit).
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public String extractAccountId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("accountId", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
