package com.whitetower.meridia.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Getter
public class Security {
    private final PasswordEncoder passwordEncoder;
    private final Integer jwtExpirationMs;
    private final SecretKey keyInBytes;

    Security(@Value("${jwt.expirationMs}") Integer jwtExpirationMs, @Value("${jwt.keyString}") String keyString){
        this.jwtExpirationMs = jwtExpirationMs;
        keyInBytes = Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
        passwordEncoder = new BCryptPasswordEncoder(13);
    }

    public String generateToken(Long id, String name) {
        return Jwts.builder().claims().subject(String.valueOf(id)).and().claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(keyInBytes, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(keyInBytes).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
