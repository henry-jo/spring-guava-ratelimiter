package com.henry.springguavaratelimiter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringGuavaRatelimiterApplication

fun main(args: Array<String>) {
	runApplication<SpringGuavaRatelimiterApplication>(*args)
}
