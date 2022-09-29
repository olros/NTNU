package ntnu.idatt2105.security.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws

data class JwtRefreshToken(
    private val token: String,
    private val claims: Jws<Claims>
) : JwtToken {

    companion object {
        fun of(token: RawJwtAccessToken, signingKey: String?): JwtRefreshToken? {
            val claims: Jws<Claims>? = token.parseClaims(signingKey)!!
            val scopes: MutableList<*>? = claims?.getBody()?.get("scopes", MutableList::class.java)
            val hasRefreshTokenScope = scopes != null && scopes.stream()
                    .anyMatch(Scopes.REFRESH_TOKEN.scope()::equals)
            return if (hasRefreshTokenScope) JwtRefreshToken(token.getToken()!!, claims!!) else null
        }
    }

    override fun getToken(): String {
        return token
    }

    fun getSubject(): String {
        return claims.getBody().getSubject()
    }

    fun getJti(): String {
        return claims.getBody().getId()
    }
}
