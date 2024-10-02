package kr.co.jumso.domain.member.repository

import kr.co.jumso.domain.member.entity.TemporaryMember
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import org.springframework.data.jpa.repository.JpaRepository

interface TemporaryMemberRepository: JpaRepository<TemporaryMember, Long>, TemporaryMemberRepositoryCustom

interface TemporaryMemberRepositoryCustom {
    fun findByEmail(email: String): TemporaryMember?

    fun existsByEmail(email: String): Boolean
}

class TemporaryMemberRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): TemporaryMemberRepositoryCustom {
    override fun findByEmail(email: String): TemporaryMember? {
        return queryFactory.selectQuery {
            select(entity(TemporaryMember::class))
            from(entity(TemporaryMember::class))
            where(column(TemporaryMember::email).equal(email))
        }.resultList.firstOrNull()
    }

    override fun existsByEmail(email: String): Boolean {
        return queryFactory.selectQuery {
            select(entity(TemporaryMember::class))
            from(entity(TemporaryMember::class))
            where(column(TemporaryMember::email).equal(email))
        }.resultList.isNotEmpty()
    }
}
