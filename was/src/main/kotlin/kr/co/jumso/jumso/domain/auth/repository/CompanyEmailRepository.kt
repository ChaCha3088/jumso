package kr.co.jumso.domain.auth.repository

import com.example.jumso.domain.auth.entity.CompanyEmail
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyEmailRepository: JpaRepository<CompanyEmail, Long>, CompanyEmailRepositoryCustom

interface CompanyEmailRepositoryCustom

class CompanyEmailRepositoryImpl: CompanyEmailRepositoryCustom {

}
