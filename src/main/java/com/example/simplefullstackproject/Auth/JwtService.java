package com.example.simplefullstackproject.Auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JWSAlgorithm alg = JWSAlgorithm.HS256;
    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final int expTime = 360; // 1 hour

    public JwtService(@Value("${jws.sharedKey}") String sharedKey) throws Exception {
        signer = new MACSigner(sharedKey.getBytes());
        verifier = new MACVerifier(sharedKey.getBytes());
    }

    public String createSignedJWT(String username, List<String> authorities) {
        JWSHeader header = new JWSHeader(alg);

        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .expirationTime(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
                .claim("authorities", authorities)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimSet);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return signedJWT.serialize();
    }

    public void verifySignature(SignedJWT signedJWT) {
        try {
            boolean verified = signedJWT.verify(verifier);
            if(!verified)
                throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
        }catch (JOSEException ex){
            throw new JwtAuthenticationException("JWT not verified - token: " + signedJWT.serialize());
        }
    }

    public void verifyExpirationTime(SignedJWT signedJWT) {
        try {
            boolean expired = signedJWT.getJWTClaimsSet()
                    .getExpirationTime().before(Date.from(Instant.now()));

            if (expired)
                throw new JwtAuthenticationException("JWT expired");
        }catch (ParseException ex){
            throw new JwtAuthenticationException("JWT does not have exp time");
        }
    }

    public Authentication authentication(SignedJWT signedJWT) {
        String subject;
        List<SimpleGrantedAuthority> authorities;
        try {
            subject = signedJWT.getJWTClaimsSet().getSubject();
            authorities = signedJWT.getJWTClaimsSet().getStringListClaim("authorities")
                    .stream().map(SimpleGrantedAuthority::new).toList();
            return new UsernamePasswordAuthenticationToken(subject, null, authorities);
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Missing claims subject or authorities");
        }
    }
}