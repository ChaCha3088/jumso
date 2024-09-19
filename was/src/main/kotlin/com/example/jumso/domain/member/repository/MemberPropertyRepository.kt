package com.example.jumso.domain.member.repository

import com.example.jumso.domain.member.entity.MemberProperty
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberPropertyRepository: JpaRepository<MemberProperty, Long>, MemberPropertyCustomRepository

interface MemberPropertyCustomRepository {
    fun findByMemberId(memberId: Long): MutableSet<MemberProperty>
}

class MemberPropertyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory
): MemberPropertyCustomRepository {
    override fun findByMemberId(memberId: Long): MutableSet<MemberProperty> {
        return queryFactory.listQuery<MemberProperty> {
            select(entity(MemberProperty::class))
            from(entity(MemberProperty::class))
            where(column(MemberProperty::memberId).equal(memberId))
        }.toMutableSet()
    }
}
