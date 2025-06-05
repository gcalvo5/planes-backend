package com.planes.services

import com.planes.dtos.CustomUserDetails
import com.planes.repositories.UsersRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val usersRepository: UsersRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        return usersRepository.findByEmail(email)
            ?.let { CustomUserDetails(it) }
            ?: throw UsernameNotFoundException("Usuario no encontrado con email: $email")
    }
}
