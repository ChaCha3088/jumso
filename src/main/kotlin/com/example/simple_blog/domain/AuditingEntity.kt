package com.example.simple_blog.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.GenerationType.IDENTITY
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class AuditingEntity: AuditingEntityId() {
    @Column(nullable = false, updatable = false)
    @CreatedDate
    lateinit var createdAt: LocalDateTime
        protected set

    @Column(nullable = false)
    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime
        protected set
}

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class AuditingEntityId {
    @Id @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null
        protected set
}