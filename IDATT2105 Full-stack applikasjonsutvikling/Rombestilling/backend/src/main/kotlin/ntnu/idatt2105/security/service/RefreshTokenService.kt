package ntnu.idatt2105.security.service

import ntnu.idatt2105.security.token.JwtRefreshToken
import ntnu.idatt2105.security.token.JwtToken
import ntnu.idatt2105.security.token.RefreshToken

interface RefreshTokenService {
    fun saveRefreshToken(token: JwtToken): RefreshToken

    fun getByJti(jti: String): RefreshToken

    fun invalidateSubsequentTokens(jti: String)

    fun rotateRefreshToken(oldRefreshToken: JwtRefreshToken, newRefreshToken: JwtRefreshToken)
}
