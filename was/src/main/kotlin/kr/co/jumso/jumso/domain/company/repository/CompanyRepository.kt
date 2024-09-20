package kr.co.jumso.domain.company.repository

import com.example.jumso.domain.company.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long>, CompanyCustomRepository

interface CompanyCustomRepository {
}

class CompanyCustomRepositoryImpl: CompanyCustomRepository {
}
