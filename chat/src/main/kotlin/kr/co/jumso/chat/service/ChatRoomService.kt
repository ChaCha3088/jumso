package kr.co.jumso.chat.service

import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.repository.ChatRoomRepository
import kr.co.jumso.domain.chat.repository.MemberChatRoomRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import kr.co.jumso.annotation.Valid
import kr.co.jumso.domain.chat.dto.*
import kr.co.jumso.domain.chat.enumstorage.MessageStatus.SUCCESS
import kr.co.jumso.domain.chat.enumstorage.MessageType.SYSTEM
import kr.co.jumso.enumstorage.RedisKeys.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val memberChatRoomRepository: MemberChatRoomRepository,

    private val redisTemplate: RedisTemplate<String, Any>,

    private val idGenerator: IdGenerator,
    private val objectMapper: ObjectMapper,
) {
    private val systemName = "system"

    fun findChatRoomByMemberId(memberId: Long): List<ChatRoomResponse> {
        return chatRoomRepository.findChatRoomsByMemberId(memberId)
            .map { chatRoom ->
                ChatRoomResponse(
                    chatRoom.id!!,
                    chatRoom.title,
                )
            }
    }

    @Transactional
    fun createChatRoom(memberId: Long, @Valid createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse {
        var chatRoom: ChatRoom? = null

        // 채팅 상대 추가
        createChatRoomRequest.targets.add(memberId)

        when (createChatRoomRequest.targets.size) {
            0, 1 -> {
                throw IllegalArgumentException("채팅 상대가 존재하지 않습니다.")
            }
            2 -> {
                // 1:1 채팅
                // 상대와 채팅방이 존재하는지 확인
                chatRoomRepository.findExistingOneToOneChatRoom(
                    createChatRoomRequest.targets.first(),
                    createChatRoomRequest.targets.last()
                )
                    // 존재하면
                    ?.let {
                        chatRoom = it

                        return ChatRoomResponse(
                            chatRoomId = chatRoom!!.id!!,
                            title = chatRoom!!.title,
                        )
                    }
                    // 존재하지 않으면
                    ?: run {
                        // 채팅방 생성
                        chatRoom = createChatRoom(createChatRoomRequest)
                    }
            }
            in 3..99 -> {
                // 그룹 채팅
                // 상대들과 채팅방이 존재하는지 확인하지 않고 채팅방 생성
                chatRoom = createChatRoom(createChatRoomRequest)
            }
            else -> {
                throw IllegalArgumentException("채팅 상대는 최대 99명까지 가능합니다.")
            }
        }

        val newMembersId = chatRoom!!.memberChatRooms.map {
            it.memberId
        }.toMutableSet()

        // redis에 채팅방 참여 회원 정보 저장
        newMembersId.map {
            redisTemplate.opsForSet().add("${CHAT_ROOM_MEMBERS}${chatRoom!!.id}", it.toString())
        }

        // redis에 채팅방 생성 요청
        // 채팅방에 "채팅방이 생성되었습니다." 메시지 추가
        val newMessageId = idGenerator.nextId()
        val newChatMessage = ChatMessage(
            chatId = newMessageId,

            senderId = memberId,
            senderName = systemName,

            message = "채팅방이 생성되었습니다."
        )

        val systemResponseMessage = ResponseMessage(
            status = SUCCESS,
            type = SYSTEM,
            data = newChatMessage
        )

        // Json으로 변환
        val jsonChatMessage = objectMapper.writeValueAsString(newChatMessage)

        // 채팅방에 메시지 추가
        redisTemplate.opsForZSet().add(CHAT_MESSAGE_STORAGE.key + chatRoom!!.id, jsonChatMessage, newMessageId.toDouble())

        // redis에 회원이 속한 채팅 서버를 조회하고
        redisTemplate.opsForHash<String, String>().get(MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID.key, memberId.toString())
            ?.let {
                // ToDo: 해당 Kafka에 메시지 발행
            }
            ?: run {
                // ToDo: 아니면 푸시 알림 요청
            }

        return ChatRoomResponse(
            chatRoomId = chatRoom!!.id!!,
            title = chatRoom!!.title,
        )
    }

    @Transactional
    fun deleteChatRoom(memberId: Long, @Valid deleteChatRoomRequest: DeleteChatRoomRequest): ChatRoomResponse {
        // memberId가 들어가있고, chatRoomId가 일치하는 member chat room id 조회
//        memberChatRoomRepository.






        // member chat room 삭제 후
        // chat room 삭제

        // redis에서 채팅방 참여 회원 정보 삭제
        // redis에서 채팅방 메시지 삭제

        return ChatRoomResponse(
            chatRoomId = 0,
            title = "",
        )
    }

    private fun createChatRoom(
        createChatRoomRequest: CreateChatRoomRequest,
    ): ChatRoom {
        val chatRoom = ChatRoom(
            title = createChatRoomRequest.title,
            newMemberIds = createChatRoomRequest.targets
        )

        return chatRoomRepository.save(chatRoom)
    }
}
