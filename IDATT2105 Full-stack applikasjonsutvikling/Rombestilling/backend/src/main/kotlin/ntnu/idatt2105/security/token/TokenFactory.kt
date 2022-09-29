package ntnu.idatt2105.security.token

import ntnu.idatt2105.user.service.UserDetailsImpl

interface TokenFactory {
    fun createAccessToken(userDetails: UserDetailsImpl): JwtAccessToken
    fun createRefreshToken(userDetails: UserDetailsImpl): JwtToken
}
