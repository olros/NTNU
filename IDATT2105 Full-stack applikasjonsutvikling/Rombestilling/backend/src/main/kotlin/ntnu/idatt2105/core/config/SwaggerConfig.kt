package ntnu.idatt2105.core.config

import ntnu.idatt2105.security.config.JWTConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig(val jwtConfig: JWTConfig) {

    private fun apiKey(): ApiKey {
        return ApiKey(jwtConfig.prefix, jwtConfig.header, "header")
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope
        return listOf(SecurityReference(jwtConfig.prefix, authorizationScopes))
    }

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(apiKey()))
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo(
            "GIDD API",
            "API Documentation for GIDD backend.",
            "1.0",
            "Terms of service",
            Contact("Mads Lundegaard", "www.madslun.com", "baregidd@gmail.com"),
            "License of API",
            "API license URL", emptyList()
        )
    }
}
