package com.planes.configuration

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy
import org.springframework.security.web.context.NullSecurityContextRepository
import org.springframework.security.web.context.SecurityContextPersistenceFilter

@Configuration
@EnableWebSecurity
class SecurityConfig (private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Value("\${google.client.id}")
    private lateinit var googleClientId: String

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Order(1)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/api/**", "/users/**")  // Especifica los paths que quieres proteger
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)  // Cambiamos a addFilterAt
            csrf { disable() }
            cors { }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize("/api/auth/**", permitAll)
                authorize("/api/test/public", permitAll)
                authorize("/error", permitAll)
                authorize("/users/**", hasAuthority("ROLE_USER"))
                authorize("/api/test/**", permitAll) // Temporalmente permitimos todo para pruebas
                authorize(anyRequest, authenticated)
            }
            headers {
                frameOptions { disable() }
            }
            anonymous { disable() }
            securityContext {
                requireExplicitSave = false
            }
        }

        return http.build()
    }




    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }


    @Bean
    fun googleIdTokenVerifier(): GoogleIdTokenVerifier {
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        return GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(googleClientId))
            .build()
    }


}
