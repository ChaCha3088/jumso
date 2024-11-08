package kr.co.jumso.auth.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import kr.co.jumso.auth.entity.CompanyEmail
import kr.co.jumso.company.entity.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyEmailRepository: JpaRepository<CompanyEmail, Long>, CompanyEmailRepositoryCustom

interface CompanyEmailRepositoryCustom {
    fun findAllByCompanyId(companyId: Long): Set<CompanyEmail>
}

class CompanyEmailRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): CompanyEmailRepositoryCustom {
    override fun findAllByCompanyId(companyId: Long): Set<CompanyEmail> {
        return queryFactory.listQuery<CompanyEmail> {
            select(entity(CompanyEmail::class))
            from(entity(CompanyEmail::class))
            where(column(CompanyEmail::company).nested(Company::id).equal(companyId))
        }.toMutableSet()
    }
}
