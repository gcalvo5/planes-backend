package com.planes.configuration

import com.planes.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter(){
    private val logger = KotlinLogging.logger {}
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)
            val userEmail = jwtService.extractUsername(jwt)

            if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
                logger.debug { "Procesando token JWT para usuario: $userEmail" }

                val userDetails = userDetailsService.loadUserByUsername(userEmail)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    logger.debug { "Token válido para usuario: $userEmail" }

                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }

                    // Verificar que la autenticación está correctamente configurada
                    logger.debug { "Autenticación creada - isAuthenticated: ${authToken.isAuthenticated}" }
                    logger.debug { "Autoridades en la autenticación: ${authToken.authorities}" }



                    SecurityContextHolder.getContext().authentication = authToken

                    logger.debug { "Autenticación establecida para usuario: $userEmail" }
                    filterChain.doFilter(request, response)
                    return
                }
            }
            // Si llegamos aquí, el token no es válido
            SecurityContextHolder.clearContext()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido")


        } catch (e: Exception) {
            logger.error(e) { "Error procesando autenticación" }
            SecurityContextHolder.clearContext()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error de autenticación")
        }
    }
}
