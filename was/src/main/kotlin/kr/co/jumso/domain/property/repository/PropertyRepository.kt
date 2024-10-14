package kr.co.jumso.domain.property.repository

import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import kr.co.jumso.domain.property.entity.Property
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PropertyRepository: JpaRepository<Property, Long>, PropertyCustomRepository

interface PropertyCustomRepository

class PropertyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): PropertyCustomRepository
