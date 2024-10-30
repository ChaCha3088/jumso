package kr.co.jumso

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class AuditingEntity(
    id: Long? = null,
): AuditingEntityId(
    id = id,
) {
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
abstract class AuditingEntityId(
    id: Long? = null
) {
    @Id @GeneratedValue(strategy = IDENTITY)
    var id: Long? = id
        protected set
}
