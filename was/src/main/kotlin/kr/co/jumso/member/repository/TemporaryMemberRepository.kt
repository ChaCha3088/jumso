package kr.co.jumso.member.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import kr.co.jumso.member.entity.TemporaryMember
import org.springframework.data.jpa.repository.JpaRepository

interface TemporaryMemberRepository: JpaRepository<TemporaryMember, Long>, TemporaryMemberRepositoryCustom

interface TemporaryMemberRepositoryCustom {
    fun findByEmail(email: String): TemporaryMember?
    fun findVerifiedById(id: Long): TemporaryMember?

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

    override fun findVerifiedById(id: Long): TemporaryMember? {
        return queryFactory.selectQuery {
            select(entity(TemporaryMember::class))
            from(entity(TemporaryMember::class))
            where(column(TemporaryMember::id).equal(id)
                .and(column(TemporaryMember::verified).equal(true)))
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
