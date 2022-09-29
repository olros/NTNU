package com.ntnu.gidd.security.token;


import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Factory class to create jwt tokens.
 */
@Component
@AllArgsConstructor
public class JwtTokenFactory implements TokenFactory {

    private JWTConfig jwtConfig;

    @Override
    public JwtAccessToken createAccessToken(UserDetailsImpl userDetails) {
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .claim("uuid", userDetails.getId())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();

        return new JwtAccessToken(token);
    }

    @Override
    public JwtToken createRefreshToken(UserDetailsImpl userDetails) {
        String token = buildRefreshToken(userDetails);

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token);

        return new JwtRefreshToken(token, claims);
    }

    private String buildRefreshToken(UserDetailsImpl userDetails) {
        Claims claims = Jwts.claims()
                .setSubject(userDetails.getUsername());
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.scope()));

        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID()
                               .toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpiration()))
                .claim("uuid", userDetails.getId())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }
}
