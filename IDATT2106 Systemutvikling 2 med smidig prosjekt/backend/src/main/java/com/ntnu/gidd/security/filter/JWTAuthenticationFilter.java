package com.ntnu.gidd.security.filter;

import com.ntnu.gidd.security.UserDetailsImpl;
import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private JWTConfig jwtConfig;
    private JwtUtil jwtUtil;

    /**
     * Read authentication from request header and add to security context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = extractAuthorizationHeaderFromRequest(request);
        log.debug("Filtering Jwt from request header: {}", header);

        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        processAuthentication(request);
        chain.doFilter(request, response);
    }

    private String extractAuthorizationHeaderFromRequest(HttpServletRequest request) {
        return request.getHeader(jwtConfig.getHeader());
    }

    /**
     * Read the JWT from Authorization header and validate the token.
     */
    private void processAuthentication(HttpServletRequest request) {
        String token = extractAuthorizationHeaderFromRequest(request);

        try {
            setAuthenticationFromToken(token);
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired", ex);
            request.setAttribute("exception", ex);
        } catch (BadCredentialsException ex) {
            log.error("Error while authenticating user:", ex);
            request.setAttribute("exception", ex);
        } catch (Exception ex) {
            log.error("Error while authenticating user, clearing security context: ", ex);
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthenticationFromToken(String token) {
        if (tokenIsValid(token)) {
            setAuthentication(token);
        }
    }

    private boolean tokenIsValid(String token) {
        return token != null && jwtUtil.isValidToken(token);
    }

    private void setAuthentication(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        UUID id = jwtUtil.getUserIdFromToken(token);
        UserDetails userDetails = UserDetailsImpl.builder()
                .id(id)
                .email(email)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, new ArrayList<>());

        log.info("Authenticating user: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
