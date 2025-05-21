package com.planes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlanesBackendApplication

fun main(args: Array<String>) {
    runApplication<PlanesBackendApplication>(*args)
}
