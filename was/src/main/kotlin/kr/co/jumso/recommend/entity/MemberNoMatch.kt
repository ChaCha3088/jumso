package kr.co.jumso.recommend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import kr.co.jumso.AuditingEntity
import kr.co.jumso.member.entity.Member

@Entity
class MemberNoMatch(
    memberId: Long,
    noMatchMemberId: Long,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId: Long = memberId
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var noMatchMember: Member? = null
        protected set

    @Column(name = "no_match_member_id")
    var noMatchMemberId = noMatchMemberId
        protected set
}
