package com.whitetower.meridia.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

@Component
@Getter
public class Security {
    private final PasswordEncoder passwordEncoder;
    private final Integer jwtExpirationMs;
    private final SecretKey signingKeyInBytes;
    private final SecretKey contentEncryptionKeyInBytes;


    Security(@Value("${jwt.expirationMs}") Integer jwtExpirationMs,
             @Value("${jwt.signing.key}") String signingKey,
             @Value("${jwe.content.encryption.key}") String contentEncryptionKey){
        this.jwtExpirationMs = jwtExpirationMs;
        signingKeyInBytes = Keys.hmacShaKeyFor(HexUtils.fromHexString(signingKey));
        contentEncryptionKeyInBytes = new SecretKeySpec(HexUtils.fromHexString(contentEncryptionKey), "AES")  ;
        passwordEncoder = new BCryptPasswordEncoder(13);
    }

    public String generateJwt(Long id, String name) {
        return Jwts.builder().claims().subject(String.valueOf(id)).and().claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(signingKeyInBytes, Jwts.SIG.HS256)
                .compact();
    }

    public Boolean validateJwt(String token) {
        try {
            Jwts.parser().verifyWith(signingKeyInBytes).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     @deprecated The id should not be directly passed by the client. We infer it via the JWT
     **/
    @Deprecated
    public Boolean validateJwtAndSubject(String token, Long id){
        try {
            Long idInToken = Long.valueOf(Jwts.parser().verifyWith(signingKeyInBytes).build().parseSignedClaims(token).getPayload().getSubject());
            return idInToken.equals(id);
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns null if JWT is invalid else it returns the subject **/
    public Optional<Long> validateJwtAndGetSubject(String token){
        try {
            return Optional.of(Long.valueOf(Jwts.parser().verifyWith(signingKeyInBytes).build().parseSignedClaims(token).getPayload().getSubject()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    // JWE //
    public String generateJwe(Long id, String name) {
        return Jwts.builder().claims().subject(String.valueOf(id)).and().claim("name", name)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .encryptWith(contentEncryptionKeyInBytes, Jwts.KEY.A256KW, Jwts.ENC.A192CBC_HS384)
                .compact();
    }

    public Boolean validateJwe(String token) {
        try{
            Jwts.parser().decryptWith(contentEncryptionKeyInBytes).build().parseEncryptedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
    @deprecated The id should not be directly passed by the client. We infer it via the JWE
    **/
    @Deprecated
    public Boolean validateJweAndSubject(String token, Long id) {
        try{
            Long idInSubject = Long.valueOf(Jwts.parser().decryptWith(contentEncryptionKeyInBytes).build().parseEncryptedClaims(token).getPayload().getSubject());
            return idInSubject.equals(id);
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns null if JWE is invalid else it returns the subject **/
    public Optional<Long> validateJweGetSubject(String token) {
        try{
            Long idInSubject = Long.valueOf( Jwts.parser().decryptWith(contentEncryptionKeyInBytes).build().parseEncryptedClaims(token).getPayload().getSubject());
            return Optional.of(idInSubject);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
