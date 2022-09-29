package ntnu.idatt2105.security.service

import ntnu.idatt2105.security.dto.JwtTokenResponse

interface JwtService {
    fun refreshToken(header: String): JwtTokenResponse
}
