package ntnu.idatt2105.security.filter

import io.jsonwebtoken.ExpiredJwtException
import ntnu.idatt2105.security.JwtUtil
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(val jwtConfig: JWTConfig, val jwtUtil: JwtUtil) : OncePerRequestFilter() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = extractAuthorizationHeaderFromRequest(request)
        if (header != null && header.startsWith(jwtConfig.prefix)) {
            processAuthentication(request)
            chain.doFilter(request, response)
            return
        }
        chain.doFilter(request, response)
        return
    }

    private fun extractAuthorizationHeaderFromRequest(request: HttpServletRequest): String? {
        return request.getHeader(jwtConfig.header)
    }

    private fun processAuthentication(request: HttpServletRequest) {
        try {
            val token = extractAuthorizationHeaderFromRequest(request) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            setAuthenticationFromToken(token)
        } catch (ex: ExpiredJwtException) {
            request.setAttribute("exception", ex)
        } catch (ex: BadCredentialsException) {
            request.setAttribute("exception", ex)
        } catch (ex: Exception) {
            SecurityContextHolder.clearContext()
        }
    }

    private fun setAuthenticationFromToken(token: String) {
        if (tokenIsValid(token)) {
            setAuthentication(token)
        }
    }

    private fun tokenIsValid(token: String?): Boolean {
        return token != null && jwtUtil.isValidToken(token)
    }

    private fun setAuthentication(token: String) {
        val email: String? = jwtUtil.getEmailFromToken(token)
        val id: UUID? = jwtUtil.getUserIdFromToken(token)
        val roles: List<SimpleGrantedAuthority> = jwtUtil.getRolesFromToken(token)
        val userDetails: UserDetails = UserDetailsImpl(id!!, email!!, "", setOf())
        val authentication = UsernamePasswordAuthenticationToken(
                userDetails, null, roles)
        SecurityContextHolder.getContext().authentication = authentication
    }
}
