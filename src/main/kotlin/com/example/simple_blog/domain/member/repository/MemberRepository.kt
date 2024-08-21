package com.example.simple_blog.domain.member.repository

import com.example.simple_blog.domain.member.entity.Member
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.deleteQuery
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import jakarta.persistence.criteria.JoinType.LEFT
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long>, MemberCustomRepository

interface MemberCustomRepository {
    fun findNotDeletedMembers(): List<Member>
    fun findNotDeletedByEmailWithRefreshToken(email: String): Member?

    fun findNotDeletedByIdWithRefreshToken(id: Long): Member?

    fun deleteByMemberId(memberId: Long)
}

class MemberCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): MemberCustomRepository {
    override fun findNotDeletedMembers(): List<Member> {
        return queryFactory.listQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::isDeleted).equal(false))
            orderBy(ExpressionOrderSpec(column(Member::id), true))
        }
    }

    override fun findNotDeletedByEmailWithRefreshToken(email: String): Member? {
        return queryFactory.singleQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::email).equal(email))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::refreshToken, joinType = LEFT)
        }
    }

    override fun findNotDeletedByIdWithRefreshToken(id: Long): Member? {
        return queryFactory.singleQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(id))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::refreshToken, joinType = LEFT)
        }
    }

    override fun deleteByMemberId(memberId: Long) {
        queryFactory.deleteQuery<Member> {
            where(column(Member::id).equal(memberId))
        }
    }
}
