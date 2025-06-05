package com.planes.controllers

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.planes.dtos.AuthResponse
import com.planes.dtos.GoogleLoginRequest
import com.planes.services.JwtService
import com.planes.services.UsersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping


@RestController
@RequestMapping("/api/auth")
class AuthController {

    @Value("\${google.client.id}")
    private lateinit var googleClientId: String

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var googleTokenVerifier: GoogleIdTokenVerifier

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var userService: UsersService

    @PostMapping("/google")
    fun googleLogin(@RequestBody request: GoogleLoginRequest): ResponseEntity<AuthResponse> {
        try {
            val idToken = googleTokenVerifier.verify(request.idToken)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    AuthResponse("", 0, "null", "Token inválido")
                )

            val payload = idToken.payload
            val email = payload.email
            val name = payload["name"] as String?

            val user = userService.findOrCreateGoogleUser(email, name ?: email)
            val token = jwtService.generateToken(user)

            return ResponseEntity.ok(
                AuthResponse(
                    token = token,
                    userId = user.id,
                    email = user.email,
                    username = user.username
                )
            )
        } catch (e: Exception) {
            logger.error(e) { "Error in googleLogin: ${e.message}" }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                e.message?.let { AuthResponse("null", 0, "null", it) }
            )
        }
    }

        @Profile("dev")
    @PostMapping("/google/test")
    fun googleLoginTest(): ResponseEntity<AuthResponse> {
        // Simula un usuario de prueba
        val user = userService.findOrCreateGoogleUser("test@example.com", "Test User")
        val token = jwtService.generateToken(user)

        return ResponseEntity.ok(AuthResponse(
            token = token,
            userId = user.id,
            email = user.email,
            username = user.username
        ))
    }
}
