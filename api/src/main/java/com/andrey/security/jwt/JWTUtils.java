package com.andrey.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.jsonwebtoken.Claims.SUBJECT;

@Component
@RequiredArgsConstructor
public class JWTUtils {
    public static final String CREATION_DATE = "created";
    public static final String JWT = "JWT";
    public static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;

    private final JWTPropertiesConfig jwtPropertiesConfig;

    private Map<String, Object> generateJWTHeaders() {
        Map<String, Object> jwtHeaders = new LinkedHashMap<>();
        jwtHeaders.put("typ", JWT);
        jwtHeaders.put("alg", ALGORITHM.getValue());

        return jwtHeaders;
    }

    private Date generateExpirationDate() {
        return new Date( new Date().getTime() + jwtPropertiesConfig.getExpiration());
    }

    private String generateToken(Map<String, Object> claims) {

        return Jwts
                .builder()
                /*Set headers with algo and token type info*/
                .setHeader(generateJWTHeaders())
                /*We create payload with user info, roles, expiration date of token*/
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                /*Signature*/
                .signWith(ALGORITHM, jwtPropertiesConfig.getSecret())
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(jwtPropertiesConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUBJECT, userDetails.getUsername());
        claims.put(CREATION_DATE, new Date());
        return generateToken(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            getUsernameFromToken(token);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }



}
