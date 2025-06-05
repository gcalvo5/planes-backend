package com.planes.repositories

import com.planes.models.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}