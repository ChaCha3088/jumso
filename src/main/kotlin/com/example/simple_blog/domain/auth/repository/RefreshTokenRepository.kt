package com.example.simple_blog.domain.auth.repository

import com.example.simple_blog.domain.auth.entity.RefreshToken
import com.example.simple_blog.domain.member.entity.Member
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.associate
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.deleteQuery
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.hibernate.query.results.Builders.entity
import org.springframework.data.jpa.domain.Specification.where
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository: JpaRepository<RefreshToken, Long>, RefreshTokenCustomRepository

interface RefreshTokenCustomRepository {
    fun findByMemberIdAndRefreshToken(memberId: Long, refreshToken: String): RefreshToken?

    fun deleteByMemberId(memberId: Long)
}

class RefreshTokenCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): RefreshTokenCustomRepository {
    override fun findByMemberIdAndRefreshToken(
        memberId: Long,
        refreshToken: String
    ): RefreshToken? {
        return queryFactory.selectQuery<RefreshToken?> {
            select(entity(RefreshToken::class))
            from(entity(RefreshToken::class))
            where(column(RefreshToken::member).nested(Member::id).equal(memberId))
            where(column(RefreshToken::token).equal(refreshToken))
        }.resultList.firstOrNull()
    }

    override fun deleteByMemberId(memberId: Long) {
        queryFactory.deleteQuery<RefreshToken> {
            entity(RefreshToken::class)
            where(column(RefreshToken::member).nested(Member::id).equal(memberId))
        }.executeUpdate()
    }
}
