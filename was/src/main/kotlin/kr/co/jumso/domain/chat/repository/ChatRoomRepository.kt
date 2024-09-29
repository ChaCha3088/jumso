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
    fun findChatRoomsWithMembersByMemberId(memberId: Long): List<ChatRoom>

    fun findExistingOneToOneChatRoom(memberId: Long, targetId: Long): ChatRoom?
}

class ChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,

    private val entityManager: EntityManager,

    private val context: JpqlRenderContext,
    private val renderer: JpqlRenderer,
): ChatRoomCustomRepository {
    override fun findChatRoomsWithMembersByMemberId(memberId: Long): List<ChatRoom> {
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

    override fun findExistingOneToOneChatRoom(memberId: Long, targetId: Long): ChatRoom? {
//        SELECT
//            count(distinct chat_room_id)
//        FROM
//            member_chat_room
//        GROUP BY
//            chat_room_id
//        HAVING
//            COUNT(DISTINCT member_id) = 2
//                AND COUNT(CASE WHEN member_id NOT IN (10001, 10005) THEN 1 END) = 0;

        val subQuery = jpql {
            select(
                path(MemberChatRoom::chatRoom).path(ChatRoom::id)
            )
                .from(
                    entity(MemberChatRoom::class)
                )
                .groupBy(
                    path(MemberChatRoom::chatRoom).path(ChatRoom::id)
                )
                .having(
                    countDistinct(path(MemberChatRoom::memberId)).equal(2)
                        .and(count(caseWhen(path(MemberChatRoom::memberId).notIn(memberId, targetId)).then(1)).equal(0))
                )
        }

        val mainQuery = jpql {
            select(
                entity(ChatRoom::class)
            )
                .from(
                    entity(ChatRoom::class),
                    // ToDo: Query 테스트
                    join(ChatRoom::memberChatRooms)
                )
                .where(
                    path(ChatRoom::id).`in`(subQuery.asSubquery())
                )
        }

        val rendered = renderer.render(mainQuery, context)

        val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
            rendered.params.forEach { (key, value) ->
                setParameter(key, value)
            }
        }

        val resultList = jpaQuery.resultList

        return resultList.firstOrNull() as ChatRoom?
    }
}
