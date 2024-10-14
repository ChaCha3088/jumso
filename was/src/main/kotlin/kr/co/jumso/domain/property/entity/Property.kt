package kr.co.jumso.domain.property.entity

import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank
import kr.co.jumso.domain.member.entity.MemberProperty

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
