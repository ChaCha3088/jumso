package kr.co.jumso.domain.company.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import jakarta.persistence.criteria.JoinType.LEFT
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import kr.co.jumso.domain.company.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long>, CompanyCustomRepository

interface CompanyCustomRepository {
    fun findByIdWithCompanyEmails(id: Long): Company?
}

class CompanyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): CompanyCustomRepository {
    override fun findByIdWithCompanyEmails(id: Long): Company? {
        return queryFactory.selectQuery<Company> {
            select(entity(Company::class))
            from(entity(Company::class))
            where(column(Company::id).equal(id))
            fetch(Company::companyEmails, joinType = LEFT)
        }.resultList.firstOrNull()
    }
}
