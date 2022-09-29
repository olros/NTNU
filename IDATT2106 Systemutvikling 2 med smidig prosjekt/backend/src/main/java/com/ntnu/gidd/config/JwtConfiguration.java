package com.ntnu.gidd.config;

import com.ntnu.gidd.security.config.JWTConfig;
import com.ntnu.gidd.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {

    @Bean
    public JWTConfig jwtConfig() {
        return new JWTConfig();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtConfig());
    }
}
