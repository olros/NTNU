package ntnu.idatt2105.security.service

import ntnu.idatt2105.security.JwtUtil
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.security.dto.JwtTokenResponse
import ntnu.idatt2105.security.exception.InvalidJwtToken
import ntnu.idatt2105.security.exception.RefreshTokenNotFound
import ntnu.idatt2105.security.extractor.TokenExtractor
import ntnu.idatt2105.security.token.JwtAccessToken
import ntnu.idatt2105.security.token.JwtRefreshToken
import ntnu.idatt2105.security.token.TokenFactory
import ntnu.idatt2105.security.validator.TokenValidator
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class JwtServiceImpl(
    val jwtUtil: JwtUtil,
    val jwtConfig: JWTConfig,
    val tokenFactory: TokenFactory,
    val tokenExtractor: TokenExtractor,
    val tokenValidator: TokenValidator,
    val refreshTokenService: RefreshTokenService,
    val userDetailsService: UserDetailsService
) : JwtService {

    override fun refreshToken(header: String): JwtTokenResponse {
        val currentJwtRefreshToken: JwtRefreshToken = getCurrentJwtRefreshToken(header)
        doValidateToken(currentJwtRefreshToken)
        val userDetails = getUserFromToken(currentJwtRefreshToken) as UserDetailsImpl

        val accessToken: JwtAccessToken = tokenFactory.createAccessToken(userDetails)
        val refreshToken: JwtRefreshToken = tokenFactory.createRefreshToken(userDetails) as JwtRefreshToken
        refreshTokenService.rotateRefreshToken(currentJwtRefreshToken, refreshToken)
        return JwtTokenResponse(accessToken.getToken(), refreshToken.getToken())
    }

    private fun getCurrentJwtRefreshToken(header: String): JwtRefreshToken {
        val token = tokenExtractor.extract(header)
        return jwtUtil.parseToken(token) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient scopes for refresh token.")
    }

    private fun getUserFromToken(refreshToken: JwtRefreshToken): UserDetails {
        val subject: String = refreshToken.getSubject()
        return userDetailsService.loadUserByUsername(subject)
    }

    private fun doValidateToken(refreshToken: JwtRefreshToken) {
        try {
            tokenValidator.validate(refreshToken.getJti())
        } catch (ex: InvalidJwtToken) {
            refreshTokenService.invalidateSubsequentTokens(refreshToken.getJti())
            throw BadCredentialsException("Invalid refresh token", ex)
        } catch (ex: RefreshTokenNotFound) {
            refreshTokenService.invalidateSubsequentTokens(refreshToken.getJti())
            throw BadCredentialsException("Invalid refresh token", ex)
        }
    }
}
