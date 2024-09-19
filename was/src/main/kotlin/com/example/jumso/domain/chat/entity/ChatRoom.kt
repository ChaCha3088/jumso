package com.example.jumso.domain.chat.entity

import com.example.jumso.domain.AuditingEntity
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank

@Entity
class ChatRoom(
    title: String,
    newMemberIds: Set<Long>
): AuditingEntity() {
    @Column(nullable = false)
    @NotBlank
    var title: String = title
        protected set

    // 채팅 회원
    @OneToMany(mappedBy = "chatRoom", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberChatRooms: MutableSet<MemberChatRoom> = newMemberIds.map {
        MemberChatRoom(it, this)
    }.toMutableSet()
        protected set
}