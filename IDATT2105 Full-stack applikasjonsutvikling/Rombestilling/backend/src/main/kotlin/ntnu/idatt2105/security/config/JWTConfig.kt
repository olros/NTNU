package ntnu.idatt2105.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class JWTConfig(
    @Value("\${security.jwt.uri:/auth}")
    var uri: String,

    @Value("\${security.jwt.header:Authorization}")
    var header: String,

    @Value("\${security.jwt.prefix:Bearer }")
    var prefix: String,

    @Value("\${security.jwt.expiration:#{24*60*60}}")
    var expiration: Int,

    @Value("\${security.jwt.secret:JwtSecretKey}")
    var secret: String,

    @Value("\${security.jwt.refresh_expiration:#{1000*60*60*24}}")
    var refreshExpiration: Int,
)
