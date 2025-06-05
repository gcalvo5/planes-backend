package com.planes.controllers

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {
    private val logger = KotlinLogging.logger {}
    @GetMapping("/public")
    fun publicEndpoint(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "Este es un endpoint público"))
    }

    @GetMapping("/verify-token")
    fun verifyToken(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        logger.debug { "Verificando token para usuario: ${authentication.name}" }
        logger.debug { "Autoridades: ${authentication.authorities}" }
        return ResponseEntity.ok(mapOf(
            "message" to "Token válido",
            "username" to authentication.name,
            "authorities" to authentication.authorities.map { it.authority },
            "isAuthenticated" to authentication.isAuthenticated
            ))
    }
}
