package kr.co.jumso.domain.post.entity

import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank

@Entity
class Post (
    title: String,
    content: String,
    member: Member
): AuditingEntity() {
    @Column(nullable = false)
    @NotBlank
    var title: String = title
        protected set

    @Column(nullable = false)
    @NotBlank
    var content: String = content
        protected set

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member = member
        protected set
}
