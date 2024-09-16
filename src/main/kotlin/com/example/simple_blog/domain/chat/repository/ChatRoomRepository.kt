package com.example.simple_blog.domain.chat.repository

import com.example.simple_blog.domain.chat.entity.ChatRoom
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository: JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository

interface ChatRoomCustomRepository {
}

class ChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): ChatRoomCustomRepository {
}