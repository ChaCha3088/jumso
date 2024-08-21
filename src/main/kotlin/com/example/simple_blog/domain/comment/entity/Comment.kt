package com.example.simple_blog.domain.comment.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.member.entity.Member
import com.example.simple_blog.domain.post.entity.Post
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