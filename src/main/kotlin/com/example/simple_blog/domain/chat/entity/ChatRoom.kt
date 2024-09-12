package com.example.simple_blog.domain.chat.entity

import com.example.simple_blog.domain.AuditingEntity
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.OneToMany

@Entity
class ChatRoom: AuditingEntity() {
    // 채팅 회원
    @OneToMany(mappedBy = "chatRoom", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberChatRooms: MutableList<MemberChatRoom> = mutableListOf()
        protected set
}