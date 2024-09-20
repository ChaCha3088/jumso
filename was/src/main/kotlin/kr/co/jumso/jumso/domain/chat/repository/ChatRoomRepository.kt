package kr.co.jumso.domain.chat.repository

import com.example.jumso.domain.chat.entity.ChatRoom
import com.example.jumso.domain.chat.entity.MemberChatRoom
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository: JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository

interface ChatRoomCustomRepository {
    fun findChatRoomsByMemberId(memberId: Long): List<ChatRoom>
}

class ChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): ChatRoomCustomRepository {
    override fun findChatRoomsByMemberId(memberId: Long): List<ChatRoom> {
            // select chat_room.*
            // from chat_room
            // where chat_room.id in (
            //     select distinct member_chat_room.chat_room_id
            //     from member_chat_room
            //     where member_chat_room.member_id = ?
            // )
        return queryFactory.listQuery<ChatRoom> {
            select(entity(ChatRoom::class))
            from(entity(ChatRoom::class))
            where(column(ChatRoom::id).`in`(
                selectDistinct(column(MemberChatRoom::chatRoom).nested(ChatRoom::id))
                    .from(entity(MemberChatRoom::class))
                    .where(column(MemberChatRoom::memberId).equal(memberId))
            ))
        }.resultList
    }
}
