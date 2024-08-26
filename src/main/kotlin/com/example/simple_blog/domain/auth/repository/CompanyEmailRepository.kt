package com.example.simple_blog.domain.auth.repository

import com.example.simple_blog.domain.auth.entity.CompanyEmail
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyEmailRepository: JpaRepository<CompanyEmail, Long>, CompanyEmailRepositoryCustom

interface CompanyEmailRepositoryCustom

class CompanyEmailRepositoryImpl: CompanyEmailRepositoryCustom {

}
