package com.example.jumso.domain.auth.repository

import com.example.jumso.domain.auth.entity.RefreshToken
import com.example.jumso.domain.member.entity.Member
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.deleteQuery
import com.linecorp.kotlinjdsl.spring.data.selectQuery
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