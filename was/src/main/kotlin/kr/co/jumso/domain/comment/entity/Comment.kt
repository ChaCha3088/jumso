package kr.co.jumso.domain.comment.entity

import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.member.entity.Member
import kr.co.jumso.domain.post.entity.Post
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank

@Entity
class Comment (
    content: String,
    member: Member,
    post: Post
): AuditingEntity() {
    @Column(nullable = false)
    @NotBlank
    var content: String = content
        protected set

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member = member
        protected set

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Post::class)
    var post: Post = post
        protected set
}
