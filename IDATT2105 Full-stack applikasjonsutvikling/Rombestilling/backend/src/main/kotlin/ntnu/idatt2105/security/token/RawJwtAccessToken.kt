package ntnu.idatt2105.security.token

import io.jsonwebtoken.*
import ntnu.idatt2105.security.exception.JwtExpiredTokenException
import org.springframework.security.authentication.BadCredentialsException
import java.security.SignatureException

data class RawJwtAccessToken(private var token: String) : JwtToken {

    fun parseClaims(signingKey: String?): Jws<Claims>? {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
        } catch (ex: UnsupportedJwtException) {
            handleInvalidJwtToken(ex)
        } catch (ex: MalformedJwtException) {
            handleInvalidJwtToken(ex)
        } catch (ex: IllegalArgumentException) {
            handleInvalidJwtToken(ex)
        } catch (ex: SignatureException) {
            handleInvalidJwtToken(ex)
        } catch (ex: ExpiredJwtException) {
            handleExpiredJwtToken(ex)
        }
        return null
    }

    private fun handleInvalidJwtToken(ex: RuntimeException) {
        throw BadCredentialsException("Invalid jwt token: ", ex)
    }
    private fun handleInvalidJwtToken(ex: SignatureException) {
        throw BadCredentialsException("Invalid jwt token: ", ex)
    }

    private fun handleExpiredJwtToken(ex: ExpiredJwtException) {
        throw JwtExpiredTokenException(ex.message, ex)
    }

    override fun getToken(): String {
        return token
    }
}
