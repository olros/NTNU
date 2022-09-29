package com.ntnu.gidd.service.token;

import com.ntnu.gidd.exception.RefreshTokenNotFound;
import com.ntnu.gidd.model.RefreshToken;
import com.ntnu.gidd.repository.RefreshTokenRepository;
import com.ntnu.gidd.security.token.JwtRefreshToken;
import com.ntnu.gidd.security.token.JwtToken;
import com.ntnu.gidd.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Service class for administrating {@link RefreshToken}s including token rotation.
 */
@Slf4j
@AllArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    
    private JwtUtil jwtUtil;

    /**
     * Invalidate the refresh token with the given jti and all subsequent tokens.
     * @param jti id of the current token
     */
    @Override
    public void invalidateSubsequentTokens(String jti) {
        log.debug("[X] Invalidating refresh token and all subsequent tokens (jti:{})", jti);
        RefreshToken refreshToken = getByJti(jti);
        traverseAndInvalidateNextRefreshTokens(refreshToken);

        refreshToken.setValid(false);
        refreshTokenRepository.save(refreshToken);
        log.debug("[X] Successfully invalidated refresh token and all subsequent tokens (jti:{})", jti);
    }

    /**
     * Invalidate the chain of {@link RefreshToken}s.
     *
     * @param refreshToken the {@link RefreshToken} starting the chain.
     */
    private void traverseAndInvalidateNextRefreshTokens(RefreshToken refreshToken) {
        RefreshToken nextToken = refreshToken.getNext();

        while (nextToken != null) {
            nextToken.setValid(false);
            refreshTokenRepository.save(nextToken);
            nextToken = nextToken.getNext();
        }
    }

    /**
     * Invalidate the old refresh token and save the new token.
     *
     * @param oldJwtRefreshToken the old token to invalidate
     * @param nextJwtRefreshToken the new token
     */
    @Override
    public void rotateRefreshToken(JwtRefreshToken oldJwtRefreshToken, JwtRefreshToken nextJwtRefreshToken) {
        RefreshToken oldRefreshToken = getByJti(oldJwtRefreshToken.getJti());
        RefreshToken nextRefreshToken = saveRefreshToken(nextJwtRefreshToken);
        oldRefreshToken.setValid(false);
        oldRefreshToken.setNext(nextRefreshToken);
        refreshTokenRepository.save(oldRefreshToken);

        log.debug("Successfully rotated refresh tokens. Old: {}, new: {}", oldRefreshToken.getJti(), nextRefreshToken.getJti());
    }

    /**
     * Return refresh token by token id
     *
     * @param jti the token id as a String
     */
    @Override
    public RefreshToken getByJti(String jti) {
        return refreshTokenRepository.findById(UUID.fromString(jti))
                .orElseThrow(RefreshTokenNotFound::new);
    }

    /**
     * Parse, build and save a new refresh token.
     *
     * @param refreshToken the jwt token containing the token
     */
    @Override
    public RefreshToken saveRefreshToken(JwtToken refreshToken) {
        JwtRefreshToken jwtRefreshToken = parseToken(refreshToken);
        RefreshToken refreshTokenToSave = buildRefreshToken(jwtRefreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshTokenToSave);

        log.debug("[X] Successfully saved refresh token (jti: {})", savedRefreshToken.getJti());
        return savedRefreshToken;
    }

    /**
     * Parse a {@link JwtToken} for further processing, if valid.
     *
     * @param refreshToken the jwt token containing the token
     */
    public JwtRefreshToken parseToken(JwtToken refreshToken) {
        return jwtUtil.parseToken(refreshToken.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token claims."));
    }

    /**
     * Build a valid {@link RefreshToken} with the corresponding token id
     *
     * @param jwtRefreshToken the jwt token containing the token.
     */
    private RefreshToken buildRefreshToken(JwtRefreshToken jwtRefreshToken) {
        return RefreshToken.builder()
                .jti(UUID.fromString(jwtRefreshToken.getJti()))
                .isValid(true)
                .build();
    }

}
