package com.example.simple_blog.domain.property.repository

import com.example.simple_blog.domain.property.entity.Property
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PropertyRepository: JpaRepository<Property, Long>, PropertyCustomRepository

interface PropertyCustomRepository

class PropertyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): PropertyCustomRepository