package com.example.simple_blog.domain.property.entity

import com.example.simple_blog.domain.member.entity.MemberProperty
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank

@Entity
class Property(
    id: Long,
    value: String
) {
    @Id
    var id: Long = id
        protected set

//    @Column(nullable = false, unique = true, length = 50)
    @Column(nullable = false, length = 50)
    @NotBlank
    var value: String = value
        protected set

    @OneToMany(mappedBy = "property", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberProperties: MutableList<MemberProperty> = mutableListOf()
        protected set
}
