package com.planes

import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<Users, Long> {

}