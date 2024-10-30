package kr.co.jumso.chat.entity

import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import kr.co.jumso.AuditingEntity
import kr.co.jumso.member.entity.Member

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
