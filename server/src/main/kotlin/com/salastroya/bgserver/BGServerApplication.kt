package com.salastroya.bgserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BGServerApplication

fun main(args: Array<String>) {
    runApplication<BGServerApplication>(*args)
}
