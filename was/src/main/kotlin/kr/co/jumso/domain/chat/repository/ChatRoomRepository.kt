package kr.co.jumso.domain.chat.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.entity.MemberChatRoom
import kr.co.jumso.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository: JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository

interface ChatRoomCustomRepository {
    fun findChatRoomsByMemberId(memberId: Long): List<ChatRoom>
}

class ChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,

    private val entityManager: EntityManager,

    private val context: JpqlRenderContext,
    private val renderer: JpqlRenderer,
): ChatRoomCustomRepository {
    override fun findChatRoomsByMemberId(memberId: Long): List<ChatRoom> {
        val subQuery = jpql {
            select<Long>(
                path(MemberChatRoom::chatRoom).path(ChatRoom::id),
            ).from(
                entity(MemberChatRoom::class),
            ).where(
                path(MemberChatRoom::member).path(Member::id).equal(memberId),
            ).groupBy(
                path(MemberChatRoom::chatRoom).path(ChatRoom::id),
            )
        }

        val mainQuery = jpql {
            select(
                entity(ChatRoom::class)
            ).from(
                entity(ChatRoom::class)
            ).where(
                path(ChatRoom::id).`in`(subQuery.asSubquery())
            )
        }

        val rendered = renderer.render(mainQuery, context)

        val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
            rendered.params.forEach { (key, value) ->
                setParameter(key, value)
            }
        }

        return jpaQuery.resultList as List<ChatRoom>
    }
}
