package kr.co.jumso.domain.property.entity

import jakarta.persistence.*
import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.member.entity.MemberProperty
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.validation.constraints.NotBlank

@Entity
class Property(
    id: Long,
    value: String,
) {
    @Id
    var id: Long? = id
        protected set

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    var value: String = value
        protected set

    @OneToMany(mappedBy = "property", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberProperties: MutableList<MemberProperty> = mutableListOf()
        protected set
}
