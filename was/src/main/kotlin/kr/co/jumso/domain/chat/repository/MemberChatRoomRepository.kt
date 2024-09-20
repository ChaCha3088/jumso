package kr.co.jumso.domain.chat.repository

import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.entity.MemberChatRoom
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberChatRoomRepository: JpaRepository<MemberChatRoom, Long>, MemberChatRoomCustomRepository

interface MemberChatRoomCustomRepository {
    fun findExistingMemberChatRoom(memberId: Long, targetId: Long): MemberChatRoom?
}

class MemberChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
): MemberChatRoomCustomRepository {
    override fun findExistingMemberChatRoom(memberId: Long, targetId: Long): MemberChatRoom? {
        //SELECT chat_room_id
        //FROM member_chat_room
        //WHERE member_id IN (?, ?)
        //GROUP BY chat_room_id
        //HAVING COUNT(DISTINCT member_id) = 2;

        return queryFactory.selectQuery<MemberChatRoom> {
            select(entity(MemberChatRoom::class))
            from(entity(MemberChatRoom::class))
            where(column(MemberChatRoom::memberId).`in`(memberId, targetId))
            groupBy(column(MemberChatRoom::chatRoom).nested(ChatRoom::id), column(MemberChatRoom::id))
            having(countDistinct(column(MemberChatRoom::memberId)).equal(2))
        }.resultList.firstOrNull()
    }
}
