package com.ntnu.gidd.security.extractor;


import com.ntnu.gidd.security.config.JWTConfig;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;


/**
 * Extracts token from a header with the attribute:
 * Authorization: Bearer <token>
 */
@Component
@AllArgsConstructor
public class JwtHeaderTokenExtractor implements TokenExtractor {

    private JWTConfig jwtConfig;

    /**
     * Validate the header and extract the jwt token.
     * @param header the header containing the jwt token
     * @return the jwt token substring
     */
    @Override
    public String extract(String header) {
        if (header == null || header.isBlank())
            throw new AuthenticationServiceException("Authorization header cannot be blank.");

        if (header.length() < jwtConfig.getHeader().length())
            throw new AuthenticationServiceException("Invalid authorization header size.");

        return header.substring(jwtConfig.getPrefix().length());
    }
}
