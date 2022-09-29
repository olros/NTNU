package ntnu.idatt2105.security.validator

import ntnu.idatt2105.security.exception.InvalidJwtToken
import ntnu.idatt2105.security.service.RefreshTokenService
import ntnu.idatt2105.security.token.RefreshToken
import org.springframework.stereotype.Component
@Component
class JwtTokenValidator(val refreshTokenService: RefreshTokenService) : TokenValidator {

    override fun validate(jti: String) {
        val refreshToken: RefreshToken = refreshTokenService.getByJti(jti)
        if (!refreshToken.isValid) throw InvalidJwtToken()
    }
}
