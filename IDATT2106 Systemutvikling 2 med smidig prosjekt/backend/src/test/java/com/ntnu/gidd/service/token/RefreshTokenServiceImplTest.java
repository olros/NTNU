package com.ntnu.gidd.service.token;

import com.ntnu.gidd.config.JwtConfiguration;
import com.ntnu.gidd.exception.RefreshTokenNotFound;
import com.ntnu.gidd.model.RefreshToken;
import com.ntnu.gidd.repository.RefreshTokenRepository;
import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.security.token.JwtRefreshToken;
import com.ntnu.gidd.security.token.JwtTokenFactory;
import com.ntnu.gidd.security.token.TokenFactory;
import com.ntnu.gidd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;


@Import({JwtConfiguration.class})
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken;

    private RefreshToken newRefreshToken;

    private JwtRefreshToken jwtRefreshToken;

    private JwtRefreshToken newJwtRefreshToken;

    @BeforeEach
    void setUp() {
        JWTConfig jwtConfig = new JWTConfig();
        TokenFactory tokenFactory = new JwtTokenFactory(jwtConfig);
        JwtUtil jwtUtil = new JwtUtil(jwtConfig);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, jwtUtil);

        ReflectionTestUtils.setField(jwtConfig, "secret", "notasecret");
        ReflectionTestUtils.setField(jwtConfig, "expiration", 24*60*60);
        ReflectionTestUtils.setField(jwtConfig, "refreshExpiration", 24*60*60);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .email(getRandomEmail())
                .build();

        jwtRefreshToken = jwtUtil.parseToken(tokenFactory.createRefreshToken(userDetails).getToken()).get();
        refreshToken = RefreshToken.builder()
                .jti(UUID.fromString(jwtRefreshToken.getJti()))
                .isValid(true)
                .build();

        newJwtRefreshToken = jwtUtil.parseToken(tokenFactory.createRefreshToken(userDetails)
                                                                        .getToken()).get();
        newRefreshToken = RefreshToken.builder()
                .jti(UUID.fromString(newJwtRefreshToken.getJti()))
                .isValid(true)
                .build();

        lenient().when(refreshTokenRepository.save(refreshToken))
                .thenReturn(refreshToken);
        lenient().when(refreshTokenRepository.save(newRefreshToken))
                .thenReturn(newRefreshToken);
        lenient().when(refreshTokenRepository.findById(refreshToken.getJti()))
                .thenReturn(Optional.of(refreshToken));
    }

    /**
     * Test that a refresh token is saved properly.
     */
    @Test
    void testSaveRefreshTokenSavesAndReturnsRefreshToken() {
        RefreshToken actualRefreshToken = refreshTokenService.saveRefreshToken(jwtRefreshToken);

        assertThat(actualRefreshToken).isEqualTo(refreshToken);
    }

    /**
     * Test that the old refresh token is invalidated upon rotation.
     */
    @Test
    void testRotateRefreshTokenInvalidatesTheOldRefreshToken() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken);

        assertThat(refreshToken.isValid()).isFalse();
    }

    /**
     * Test that rotating refresh tokens points the old token to the new.
     */
    @Test
    void testRotateRefreshTokenPointsOldRefreshTokenToNewRefreshToken() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken);

        assertThat(refreshToken.getNext()).isEqualTo(newRefreshToken);
    }

    /**
     * Test that rotating refresh tokens saves the old refresh token.
     */
    @Test
    void testRotateRefreshTokenSavesTheOldRefreshToken() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken);

        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }

    /**
     * Test that the new refresh token is saved as valid upon rotation.
     */
    @Test
    void testRotateRefreshTokenSavesTheNewRefreshTokenAsValid() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken);

        verify(refreshTokenRepository, times(1)).save(newRefreshToken);
    }

    /**
     * Test that the correct refresh token is returned when it exists.
     */
    @Test
    void testGetByJtiWhenRefreshTokenExistsReturnsRefreshToken() {
        RefreshToken actualRefreshToken = refreshTokenService.getByJti(refreshToken.getJti()
                                                                  .toString());

        assertThat(actualRefreshToken).isEqualTo(refreshToken);
    }

    /**
     * Test that an exception is raised when the refresh token does not exist.
     */
    @Test
    void testGetByJtiWhenRefreshTokenDoesNotExistsRaisesException() {
        lenient().when(refreshTokenRepository.findById(refreshToken.getJti()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RefreshTokenNotFound.class).isThrownBy(() -> refreshTokenService.getByJti(refreshToken.getJti()
                                                                                                                    .toString()));
    }

    /**
     * Test that all subsequent tokens are invalidated when invalidating all subsequent tokens.
     */
    @Test
    void testInvalidateSubsequentTokensInvalidatesAllRefreshTokensAfterRefreshToken() {
        refreshToken.setNext(newRefreshToken);

        refreshTokenService.invalidateSubsequentTokens(refreshToken.getJti().toString());

        assertThat(refreshToken.isValid()).isFalse();
        assertThat(newRefreshToken.isValid()).isFalse();
    }
}