package com.ntnu.gidd.security.token;

import com.ntnu.gidd.exception.JwtExpiredTokenException;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;


/**
 * Class for unparsed raw jwt access tokens.
 */
@Slf4j
@AllArgsConstructor
public class RawJwtAccessToken implements JwtToken {

    private String token;

    /**
     * Parse and validate jwt signature
     * @return the jwt claims.
     */
    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            handleInvalidJwtToken(ex);
        } catch (ExpiredJwtException ex) {
            handleExpiredJwtToken(ex);
        }

        return null;
    }

    private void handleInvalidJwtToken(RuntimeException ex) {
        log.error("Invalid jwt token: {}", token, ex);
        throw new BadCredentialsException("Invalid jwt token: ", ex);
    }

    private void handleExpiredJwtToken(ExpiredJwtException ex) {
        log.info("Jwt token is expired", ex);
        throw new JwtExpiredTokenException(ex.getMessage(), ex);
    }

    @Override
    public String getToken() {
        return token;
    }
}
