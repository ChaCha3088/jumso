package com.example.simple_blog.domain.chat.repository

import com.example.simple_blog.domain.chat.entity.MemberChatRoom
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberChatRoomRepository: JpaRepository<MemberChatRoom, Long>, MemberChatRoomCustomRepository

interface MemberChatRoomCustomRepository {
}

class MemberChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): MemberChatRoomCustomRepository {
}