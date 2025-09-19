package com.hritik.auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements CommandLineRunner {

    @Value("${jwt.expiry}")
    private int expiry;

    private String createToken(Map<String, Object> payload, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry * 1000L);
        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate).subject(email)
                .signWith(getSignKey())
                .compact();
    }

    private Claims extractAllPayloads(String token) {

        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllPayloads(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSignKey() {
        String secret = System.getenv("JWT_SECRET");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Boolean validateToken(String token, String email) {
        final String userEmailFetchedFromToken = extractEmail(token);
        return (userEmailFetchedFromToken.equals(email)) && !isTokenExpired(token);
    }

    private Object extractPayload(String token, String payloadKey) {
        Claims claim = extractAllPayloads(token);
        return claim.get(payloadKey);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> mp = new HashMap<>();
        mp.put("email", "hritik@gmail.com");
        mp.put("phoneNumber", "9999999999");
        String result = createToken(mp, "hritik");
        System.out.println("Generated token is: " + result);
        System.out.println(extractPayload(result, "email").toString());
    }
}