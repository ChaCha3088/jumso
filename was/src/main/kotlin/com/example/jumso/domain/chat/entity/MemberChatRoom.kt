package com.example.jumso.domain.chat.entity

import com.example.jumso.domain.AuditingEntity
import com.example.jumso.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class MemberChatRoom(
    memberId: Long,
    chatRoom: ChatRoom,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId: Long = memberId
        protected set

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY, targetEntity = ChatRoom::class)
    var chatRoom: ChatRoom = chatRoom
        protected set

    // hashCode와 equals를 override
    override fun hashCode(): Int {
        return memberId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberChatRoom

        if (memberId != other.memberId) return false

        return true
    }
}
