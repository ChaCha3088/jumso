package kr.co.jumso

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatApplication

fun main(args: Array<String>) {
	runApplication<ChatApplication>(*args)
}