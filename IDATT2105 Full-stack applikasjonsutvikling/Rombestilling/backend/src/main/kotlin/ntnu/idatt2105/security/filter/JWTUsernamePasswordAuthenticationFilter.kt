package ntnu.idatt2105.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import ntnu.idatt2105.security.config.JWTConfig
import ntnu.idatt2105.security.dto.LoginRequest
import ntnu.idatt2105.security.service.RefreshTokenService
import ntnu.idatt2105.security.token.JwtAccessToken
import ntnu.idatt2105.security.token.JwtToken
import ntnu.idatt2105.security.token.JwtTokenFactory
import ntnu.idatt2105.security.token.TokenFactory
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTUsernamePasswordAuthenticationFilter : UsernamePasswordAuthenticationFilter {

    private var tokenFactory: TokenFactory
    private var objectMapper: ObjectMapper
    private var refreshTokenService: RefreshTokenService?
    private var jwtConfig: JWTConfig

    constructor(refreshTokenService: RefreshTokenService, authenticationManager: AuthenticationManager, jwtConfig: JWTConfig) {
        this.refreshTokenService = refreshTokenService
        this.jwtConfig = jwtConfig
        this.authenticationManager = authenticationManager
        tokenFactory = JwtTokenFactory(jwtConfig)
        objectMapper = ObjectMapper()
        setRequiresAuthenticationRequestMatcher(
                AntPathRequestMatcher(jwtConfig.uri + "/login", "POST"))
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        return try {
            val credentials: LoginRequest = ObjectMapper().readValue(request.inputStream, LoginRequest::class.java)
            val authentication = UsernamePasswordAuthenticationToken(
                    credentials.email,
                    credentials.password,
                    ArrayList())
            authenticationManager.authenticate(authentication)
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
    }

    @Throws(IOException::class)
    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse, chain: FilterChain?, auth: Authentication) {
        val userDetails: UserDetailsImpl = auth.getPrincipal() as UserDetailsImpl
        val accessToken: JwtAccessToken = tokenFactory.createAccessToken(userDetails)
        val refreshToken: JwtToken = tokenFactory.createRefreshToken(userDetails)
        refreshTokenService?.saveRefreshToken(refreshToken)
        val tokens: MutableMap<String, String> = HashMap()
        tokens.putIfAbsent("token", accessToken.getToken())
        tokens.putIfAbsent("refreshToken", refreshToken.getToken())

        response.addHeader(jwtConfig.header, "${jwtConfig.prefix} ${accessToken.getToken()}")
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        objectMapper.writeValue(response.writer, tokens)
    }
}
