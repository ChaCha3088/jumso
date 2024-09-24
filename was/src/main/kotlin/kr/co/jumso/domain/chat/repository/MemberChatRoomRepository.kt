package kr.co.jumso.domain.chat.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.entity.MemberChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberChatRoomRepository: JpaRepository<MemberChatRoom, Long>, MemberChatRoomCustomRepository

interface MemberChatRoomCustomRepository {
}

class MemberChatRoomCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,

    private val entityManager: EntityManager,

    private val context: JpqlRenderContext,
    private val renderer: JpqlRenderer,
): MemberChatRoomCustomRepository {

}
