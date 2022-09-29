package ntnu.idatt2105.security.extractor

import ntnu.idatt2105.security.config.JWTConfig
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Component

@Component
class JwtHeaderTokenExtractor(val jwtConfig: JWTConfig) : TokenExtractor {
    override fun extract(payload: String): String {
        if (payload.isBlank()) throw AuthenticationServiceException("Authorization header cannot be blank.")
        if (payload.length < jwtConfig.header.length) throw AuthenticationServiceException("Invalid authorization header size.")
        return payload.substring(jwtConfig.prefix.length)
    }
}
