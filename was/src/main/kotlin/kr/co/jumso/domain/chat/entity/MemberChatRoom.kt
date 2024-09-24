package kr.co.jumso.domain.chat.entity

import jakarta.persistence.*
import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.member.entity.Member
import jakarta.persistence.FetchType.LAZY

@Table(
    name = "member_chat_room",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["member_id", "chat_room_id"]
        )
    ]
)
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

        return memberId == other.memberId
    }
}
