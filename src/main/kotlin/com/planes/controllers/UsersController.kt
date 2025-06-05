package com.planes.controllers

import com.planes.dtos.UsersDTO
import com.planes.services.UsersService
import com.planes.models.Users
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UsersController(private val usersService: UsersService) {


    @GetMapping
    fun getUsers(): List<UsersDTO> {
        return usersService.getAllUsers()
    }

    @GetMapping("{id}")
    fun getUserById(@PathVariable id: Long): UsersDTO? {
        return usersService.getUserById(id)
    }

    @PostMapping
    fun createUser(@RequestBody users: Users) {
        usersService.insertUsers(users)
    }
}