package ntnu.idatt2105.security.exception

import org.springframework.security.core.AuthenticationException

class JwtExpiredTokenException(msg: String?, cause: Throwable?) : AuthenticationException(msg, cause)
