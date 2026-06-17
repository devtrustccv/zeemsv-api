package cv.zeemsv.api.utils;

import cv.zeemsv.api.application.generic.dto.SessionTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public final class JwtUtil {
    private JwtUtil() {
    }

    public static String generateToken(String secretKey, String subject, long expirationInHours, String email,
            String name, String fingerprint) {
        String fingerprintHash = generateSecureHash(fingerprint);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInHours * 60 * 60 * 1000);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(subject)
            .claim("email", email)
            .claim("name", name)
            .claim("fingerprint", fingerprintHash)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public static SessionTokenDto validateToken(String token, String secretKey, String expectedFingerprint) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            String fingerprint = claims.get("fingerprint", String.class);
            boolean valid = generateSecureHash(expectedFingerprint).equals(fingerprint);

            return SessionTokenDto.builder()
                .valid(valid)
                .email(claims.get("email", String.class))
                .name(claims.get("name", String.class))
                .fingerprint(fingerprint)
                .sub(claims.getSubject())
                .build();
        } catch (Exception e) {
            return SessionTokenDto.builder().valid(false).build();
        }
    }

    public static String generateSecureHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Erro ao gerar hash SHA-256", e);
        }
    }
}
