package kr.co.jumso.domain.member.repository

import com.example.jumso.domain.member.entity.TemporaryMember
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import org.springframework.data.jpa.repository.JpaRepository

interface TemporaryMemberRepository: JpaRepository<TemporaryMember, Long>, TemporaryMemberRepositoryCustom

interface TemporaryMemberRepositoryCustom {
    fun findByVerificationCode(verificationCode: String): TemporaryMember?

    fun existsByUsernameAndCompanyEmailId(username: String, companyEmailId: Long): Boolean
}

class TemporaryMemberRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): TemporaryMemberRepositoryCustom {
    override fun findByVerificationCode(verificationCode: String): TemporaryMember? {
        return queryFactory.selectQuery {
            select(entity(TemporaryMember::class))
            from(entity(TemporaryMember::class))
            where(column(TemporaryMember::verificationCode).equal(verificationCode))
        }.resultList.firstOrNull()
    }

    override fun existsByUsernameAndCompanyEmailId(username: String, companyEmailId: Long): Boolean {
        return queryFactory.selectQuery {
            select(entity(TemporaryMember::class))
            from(entity(TemporaryMember::class))
            where(column(TemporaryMember::username).equal(username))
            where(column(TemporaryMember::companyEmailId).equal(companyEmailId))
        }.resultList.isNotEmpty()
    }
}
