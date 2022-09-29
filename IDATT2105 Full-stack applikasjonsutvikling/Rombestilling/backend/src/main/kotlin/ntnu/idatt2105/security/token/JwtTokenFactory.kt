package ntnu.idatt2105.security.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import ntnu.idatt2105.core.util.SecurityConstants.AUTHORITIES_KEY
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

@Component
data class JwtTokenFactory(val jwtConfig: JWTConfig) : TokenFactory {

    @Suppress("DEPRECATION")
    override fun createAccessToken(userDetails: UserDetailsImpl): JwtAccessToken {
        val authorities = userDetails.authorities.stream()
            .map { it?.authority }
            .collect(Collectors.joining(","))

        val token: String = Jwts.builder()
                .setSubject(userDetails.username)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + jwtConfig.expiration))
                .claim("uuid", userDetails.getId())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, jwtConfig.secret)
                .compact()

        return JwtAccessToken(token)
    }

    override fun createRefreshToken(userDetails: UserDetailsImpl): JwtToken {
        val token = userDetails?.let { buildRefreshToken(it) }
        val claims: Jws<Claims> = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.secret).build()
                .parseClaimsJws(token)
        return token?.let { JwtRefreshToken(it, claims) }
    }
    @Suppress("DEPRECATION")
    private fun buildRefreshToken(userDetails: UserDetailsImpl): String {
        val claims: Claims = Jwts.claims()
                .setSubject(userDetails.getUsername())
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.scope()))

        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID()
                        .toString())
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + jwtConfig.refreshExpiration))
                .claim("uuid", userDetails.getId())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.secret)
                .compact()
    }
}
