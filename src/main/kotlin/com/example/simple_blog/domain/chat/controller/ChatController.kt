package com.example.simple_blog.domain.chat.controller

import com.example.simple_blog.domain.auth.annotation.MemberId
import com.example.simple_blog.domain.chat.dto.ChatRoomResponse
import com.example.simple_blog.domain.chat.dto.CreateChatRoomRequest
import com.example.simple_blog.domain.chat.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/chat"], consumes = ["application/json"])
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping(value = ["/open"])
    fun openChat(
        @MemberId memberId: Long,
        @Validated @RequestBody createChatRoomRequest: CreateChatRoomRequest
    ): ResponseEntity<ChatRoomResponse> {
        val createChatRoomResponse = chatService.createChatRoom(memberId, createChatRoomRequest)

        return ResponseEntity.ok(createChatRoomResponse)
    }
}
