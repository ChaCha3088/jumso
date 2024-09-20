package kr.co.jumso

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WasApplication

fun main(args: Array<String>) {
	runApplication<WasApplication>(*args)
}
