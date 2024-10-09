package kr.co.jumso.domain.member.repository

import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import kr.co.jumso.domain.member.entity.MemberNotTheseCompany
import org.springframework.data.jpa.repository.JpaRepository

interface MemberNotTheseCompanyRepository: JpaRepository<MemberNotTheseCompany, Long>, MemberNotTheseCompanyCustomRepository {
}

interface MemberNotTheseCompanyCustomRepository {
}

class MemberNotTheseCompanyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): MemberNotTheseCompanyCustomRepository {
}
