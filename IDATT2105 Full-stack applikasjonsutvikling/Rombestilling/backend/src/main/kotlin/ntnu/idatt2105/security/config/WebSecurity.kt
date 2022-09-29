package ntnu.idatt2105.security.config

import ntnu.idatt2105.security.JwtUtil
import ntnu.idatt2105.security.filter.JWTAuthenticationFilter
import ntnu.idatt2105.security.filter.JWTUsernamePasswordAuthenticationFilter
import ntnu.idatt2105.security.service.RefreshTokenService
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.util.List
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurity(
    val refreshTokenService: RefreshTokenService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val jwtConfig: JWTConfig
) : WebSecurityConfigurerAdapter() {

    private val DOCS_WHITELIST = arrayOf(
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    )

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.cors().configurationSource { request: HttpServletRequest? ->
            val cors = CorsConfiguration()
            cors.allowedOrigins = List.of("*")
            cors.allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
            cors.allowedHeaders = List.of("*")
            cors
        }
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(*DOCS_WHITELIST).permitAll()
            .antMatchers(HttpMethod.POST, jwtConfig.uri + "/login").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/forgot-password/").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/reset-password/**").permitAll()
            .antMatchers(HttpMethod.GET, "/auth/refresh-token/").permitAll()
            .antMatchers(HttpMethod.GET, "/users/me/", "/users/me/reservations/", "/users/me/groups/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.PUT, "/users/{userId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.GET, "/sections/", "/sections/{sectionId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.POST, "/sections/{sectionId}/reservations/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.GET, "/sections/{sectionId}/reservations/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.DELETE, "/sections/{sectionId}/reservations/{reservationId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.PUT, "/sections/{sectionId}/reservations/{reservationId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.GET, "/sections/{sectionId}/reservations/{reservationId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.GET, "/groups/", "/groups/{groupId}/", "/groups/{groupId}/reservations/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.DELETE, "/groups/{groupId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.PUT, "/groups/{groupId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.POST, "/groups/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.GET, "/groups/{groupId}/memberships/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.DELETE, "/groups/{groupId}/memberships/{userId}/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.POST, "/groups/{groupId}/memberships/").hasRole(RoleType.USER)
            .antMatchers(HttpMethod.POST, "/groups/{groupId}/memberships/batch-memberships/").hasRole(RoleType.USER)
            .antMatchers("/**").hasAnyRole(RoleType.ADMIN)
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { req: HttpServletRequest?, res: HttpServletResponse, e: AuthenticationException? ->
                res.contentType = "application/json"
                res.status = HttpServletResponse.SC_UNAUTHORIZED
                e?.cause
                res.outputStream.println("{ \"message\": \"Feil brukernavn eller passord\"}")
            }
            .and()
            .addFilter(JWTUsernamePasswordAuthenticationFilter(refreshTokenService, authenticationManager(), jwtConfig))
            .addFilterAfter(JWTAuthenticationFilter(jwtConfig, jwtUtil), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsServiceImpl()
    }
}
