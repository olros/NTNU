package ntnu.idatt2105.security.service

import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.security.JwtUtil
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.security.exception.RefreshTokenNotFound
import ntnu.idatt2105.security.repository.RefreshTokenRepository
import ntnu.idatt2105.security.token.JwtRefreshToken
import ntnu.idatt2105.security.token.JwtTokenFactory
import ntnu.idatt2105.security.token.RefreshToken
import ntnu.idatt2105.security.token.TokenFactory
import ntnu.idatt2105.user.service.UserDetailsImpl
import ntnu.idatt2105.user.service.UserDetailsImplBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ContextConfiguration
import java.util.*

@ContextConfiguration(classes = [JWTConfig::class, JwtUtil::class])
@ExtendWith(MockitoExtension::class)
internal class RefreshTokenServiceImplTest {

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    private lateinit var refreshTokenService: RefreshTokenService

    private lateinit var refreshToken: RefreshToken

    private lateinit var newRefreshToken: RefreshToken

    private lateinit var jwtRefreshToken: JwtRefreshToken

    private lateinit var newJwtRefreshToken: JwtRefreshToken

    private val JWT_SECRET_LENGTH: Int = 512
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    @BeforeEach
    fun setUp() {

        val randomString = (1..JWT_SECRET_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        val jwtConfig = JWTConfig(
            uri = "/auth",
            header = "Authorization",
            prefix = "Bearer",
            secret = randomString,
            expiration = 24 * 60 * 60,
            refreshExpiration = 24 * 60 * 60
        )

        val tokenFactory: TokenFactory = JwtTokenFactory(jwtConfig)
        val jwtUtil = JwtUtil(jwtConfig)
        refreshTokenService = RefreshTokenServiceImpl(refreshTokenRepository, jwtUtil)

        val userDetails: UserDetailsImpl = UserDetailsImplBuilder(
            email = Faker().internet.email()
        ).build()

        jwtRefreshToken = jwtUtil.parseToken(tokenFactory.createRefreshToken(userDetails).getToken())!!
        refreshToken = RefreshToken(
            jti = UUID.fromString(jwtRefreshToken.getJti()),
            isValid = true,
            next = null
        )

        newJwtRefreshToken = jwtUtil.parseToken(
            tokenFactory.createRefreshToken(userDetails)
                .getToken()
        )!!

        newRefreshToken = RefreshToken(
            jti = UUID.fromString(newJwtRefreshToken.getJti()),
            isValid = true,
            next = null
        )

        lenient().`when`(refreshTokenRepository.save(refreshToken))
            .thenReturn(refreshToken)
        lenient().`when`(refreshTokenRepository.save(newRefreshToken))
            .thenReturn(newRefreshToken)
        lenient().`when`(refreshTokenRepository.findById(refreshToken.jti))
            .thenReturn(Optional.of(refreshToken))
    }

    @Test
    fun `test that saving refresh token saves and returns token`() {
        val actualRefreshToken = refreshTokenService.saveRefreshToken(jwtRefreshToken)
        assertThat(actualRefreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun `test that rotating refresh token invalidates the old refresh token`() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken)
        assertThat(refreshToken.isValid).isFalse
    }

    @Test
    fun `test that rotating refresh tokens points the old token to the new`() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken)
        assertThat(refreshToken.next).isEqualTo(newRefreshToken)
    }

    @Test
    fun `test that rotating refresh tokens saves the old refresh token`() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken)
        verify(refreshTokenRepository, times(1)).save(refreshToken)
    }

    @Test
    fun `test that the new refresh token is saved as valid upon rotation`() {
        refreshTokenService.rotateRefreshToken(jwtRefreshToken, newJwtRefreshToken)
        verify(refreshTokenRepository, times(1)).save(newRefreshToken)
    }

    @Test
    fun `test that get by jti when the token exists returns the correct refresh token`() {
        val actualRefreshToken = refreshTokenService.getByJti(
            refreshToken.jti
                .toString()
        )
        assertThat(actualRefreshToken).isEqualTo(refreshToken)
    }

    @Test
    fun `test that an exception is raised when the refresh token does not exist`() {
        lenient().`when`(refreshTokenRepository.findById(refreshToken.jti))
            .thenReturn(Optional.empty())

        assertThatExceptionOfType(RefreshTokenNotFound::class.java).isThrownBy {
            refreshTokenService.getByJti(
                refreshToken.jti
                    .toString()
            )
        }
    }

    @Test
    fun `test that all subsequent tokens are invalidated when invalidating all subsequent tokens`() {
        refreshToken.next = newRefreshToken
        refreshTokenService.invalidateSubsequentTokens(refreshToken.jti.toString())

        assertThat(refreshToken.isValid).isFalse
        assertThat(newRefreshToken.isValid).isFalse
    }
}
