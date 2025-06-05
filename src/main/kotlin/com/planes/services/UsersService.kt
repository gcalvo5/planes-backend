package com.planes.services

import com.planes.dtos.UsersDTO
import com.planes.models.AuthProvider
import com.planes.models.Users
import com.planes.repositories.UsersRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UsersService (private val UsersRepository: UsersRepository, private val passwordEncoder: PasswordEncoder) {

    @Transactional
    fun findOrCreateGoogleUser(email: String, name: String): Users {
        return UsersRepository.findByEmail(email) ?: createGoogleUser(email, name)
    }

    private fun createGoogleUser(email: String, name: String): Users {
        val user = Users(
            email = email,
            username = generateUsername(name),
            password = passwordEncoder.encode(generateRandomPassword()),
            provider = AuthProvider.GOOGLE,
            enabled = true
        )
        return UsersRepository.save(user)
    }
    private fun generateUsername(name: String): String {
        val baseUsername = name.lowercase()
            .replace(" ", "")
            .replace(Regex("[^a-z0-9]"), "")

        var username = baseUsername
        var counter = 1

        while (UsersRepository.existsByUsername(username)) {
            username = "$baseUsername$counter"
            counter++
        }

        return username
    }

    private fun generateRandomPassword(): String {
        return UUID.randomUUID().toString()
    }


    fun getAllUsers(): List<UsersDTO> {
        return UsersRepository.findAll().map { user ->
            UsersDTO(
                id = user.id,
                username = user.username,
                email = user.email
            )
        }

    }
    fun getUserById(id: Long): UsersDTO? {
        return UsersRepository.findById(id).map { user ->
            UsersDTO(
                id = user.id,
                username = user.username,
                email = user.email
            )
        }.orElse(null)
    }
    fun insertUsers(users: Users){
        UsersRepository.save(users)
    }
}