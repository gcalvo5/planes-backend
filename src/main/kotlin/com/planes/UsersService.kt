package com.planes

import org.springframework.stereotype.Service

@Service
class UsersService (private val UsersRepository: UsersRepository) {

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