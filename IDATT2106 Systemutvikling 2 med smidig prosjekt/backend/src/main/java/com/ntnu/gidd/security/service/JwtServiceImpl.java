package com.ntnu.gidd.security.service;

import com.ntnu.gidd.dto.JwtTokenResponse;
import com.ntnu.gidd.exception.InvalidJwtToken;
import com.ntnu.gidd.exception.RefreshTokenNotFound;
import com.ntnu.gidd.model.User;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.security.extractor.TokenExtractor;
import com.ntnu.gidd.security.token.JwtAccessToken;
import com.ntnu.gidd.security.token.JwtRefreshToken;
import com.ntnu.gidd.security.token.TokenFactory;
import com.ntnu.gidd.security.validator.TokenValidator;
import com.ntnu.gidd.service.token.RefreshTokenService;
import com.ntnu.gidd.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@AllArgsConstructor
public class JwtServiceImpl implements JwtService {

    private JwtUtil jwtUtil;
    private JWTConfig jwtConfig;
    private TokenFactory tokenFactory;
    private TokenExtractor tokenExtractor;
    private UserRepository userRepository;
    private TokenValidator tokenValidator;
    private RefreshTokenService refreshTokenService;

    /**
     * Create a new jwt access token from the refresh token in the request header.
     *
     * @return the new jwt access token
     */
    @Override
    public JwtTokenResponse refreshToken(String header) {
        JwtRefreshToken currentJwtRefreshToken = getCurrentJwtRefreshToken(header);
        doValidateToken(currentJwtRefreshToken);

        User user = getUserFromToken(currentJwtRefreshToken);
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();

        JwtAccessToken accessToken = tokenFactory.createAccessToken(userDetails);
        JwtRefreshToken refreshToken = (JwtRefreshToken) tokenFactory.createRefreshToken(userDetails);

        refreshTokenService.rotateRefreshToken(currentJwtRefreshToken, refreshToken);

        return new JwtTokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    private JwtRefreshToken getCurrentJwtRefreshToken(String header) {
        String token = tokenExtractor.extract(header);

        return jwtUtil.parseToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token claims."));
    }

    private User getUserFromToken(JwtRefreshToken refreshToken) {
        String subject = refreshToken.getSubject();
        return userRepository.findByEmail(subject)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));
    }

    private void doValidateToken(JwtRefreshToken refreshToken) {
        try {
            tokenValidator.validate(refreshToken.getJti());
        } catch (InvalidJwtToken | RefreshTokenNotFound ex) {
            log.error("[X] Token validation failed.", ex);
            refreshTokenService.invalidateSubsequentTokens(refreshToken.getJti());
            throw new BadCredentialsException("Invalid refresh token", ex);
        }
    }
}
