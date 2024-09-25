package kr.co.jumso.domain.chat.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.deleteQuery
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.entity.MemberChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberChatRoomRepository: JpaRepository<MemberChatRoom, Long>, MemberChatRoomCustomRepository

interface MemberChatRoomCustomRepository {
    fun findMemberChatRoomIdByMemberIdAndChatRoomId(memberId: Long, chatRoomId: Long): Long?

    fun deleteByChatRoomId(chatRoomId: Long)
}

class MemberChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,

    private val entityManager: EntityManager,

    private val context: JpqlRenderContext,
    private val renderer: JpqlRenderer,
): MemberChatRoomCustomRepository {
    override fun findMemberChatRoomIdByMemberIdAndChatRoomId(memberId: Long, chatRoomId: Long): Long? {
        val query = jpql {
            select(path(MemberChatRoom::id))
                .from(entity(MemberChatRoom::class))
                .whereAnd(
                    path(MemberChatRoom::memberId).eq(memberId),
                    path(MemberChatRoom::chatRoom).path(ChatRoom::id).eq(chatRoomId)
                )
        }

        val rendered = renderer.render(query, context)

        val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
            rendered.params.forEach { (key, value) ->
                setParameter(key, value)
            }
        }

        return jpaQuery.resultList.singleOrNull()?.let {
            it as Long
        }
    }

    override fun deleteByChatRoomId(chatRoomId: Long) {
        val query = jpql {
            deleteFrom(entity(MemberChatRoom::class))
                .where(path(MemberChatRoom::chatRoom).path(ChatRoom::id).eq(chatRoomId))
        }

        val rendered = renderer.render(query, context)

        val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
            rendered.params.forEach { (key, value) ->
                setParameter(key, value)
            }
        }

        jpaQuery.executeUpdate()
    }
}
