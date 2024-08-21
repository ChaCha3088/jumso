package com.example.simple_blog.domain.member.repository

import com.example.simple_blog.domain.property.entity.MemberProperty
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberPropertyRepository: JpaRepository<MemberProperty, Long>, MemberPropertyCustomRepository

interface MemberPropertyCustomRepository

class MemberPropertyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory
): MemberPropertyCustomRepository