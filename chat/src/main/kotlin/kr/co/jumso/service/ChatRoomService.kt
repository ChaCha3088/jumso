package kr.co.jumso.service

import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.repository.ChatRoomRepository
import kr.co.jumso.domain.chat.repository.MemberChatRoomRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import kr.co.jumso.annotation.Valid
import kr.co.jumso.domain.chat.dto.*
import kr.co.jumso.domain.chat.dto.request.CreateChatRoomRequest
import kr.co.jumso.domain.chat.dto.request.DeleteChatRoomRequest
import kr.co.jumso.domain.chat.dto.response.ChatMessageResponse
import kr.co.jumso.domain.chat.dto.response.ChatRoomResponse
import kr.co.jumso.domain.chat.dto.response.CreateChatRoomResponse
import kr.co.jumso.domain.chat.dto.response.DeleteChatRoomResponse
import kr.co.jumso.domain.chat.enumstorage.MessageType.*
import kr.co.jumso.domain.chat.enumstorage.RedisKeys.*
import kr.co.jumso.enumstorage.KafkaConfig.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val memberChatRoomRepository: MemberChatRoomRepository,

    private val redisTemplate: RedisTemplate<String, Any>,
    private val kafkaTemplate: KafkaTemplate<String, Any>,

    private val idGenerator: IdGenerator,
    private val objectMapper: ObjectMapper,
) {
    fun findChatRoomByMemberId(memberId: Long): List<ChatRoomResponse> {
        val chatRooms = chatRoomRepository.findChatRoomsWithMembersByMemberId(memberId)

        val chatRoomResponses = mutableListOf<ChatRoomResponse>()

        chatRooms.forEach { chatRoom ->
            val chatRoomMembers = redisTemplate.opsForSet().members("$CHAT_ROOM_MEMBERS ${chatRoom.id!!}")

            chatRoomResponses.add(
                ChatRoomResponse(
                    chatRoomId = chatRoom.id!!,
                    memberIds = chatRoomMembers!!.map { it.toString().toLong() }.toSet(),
                    title = chatRoom.title,
                )
            )
        }

        return chatRoomResponses
    }

    @Transactional
    fun createChatRoom(memberId: Long, @Valid createChatRoomRequest: CreateChatRoomRequest) {
        var chatRoom: ChatRoom? = null

        // 채팅 상대 추가
        createChatRoomRequest.targetMemberIds.add(memberId)

        when (createChatRoomRequest.targetMemberIds.size) {
            0, 1 -> {
                throw IllegalArgumentException("채팅 상대가 존재하지 않습니다.")
            }
            2 -> {
                // 1:1 채팅
                // 상대와 채팅방이 존재하는지 확인
                chatRoomRepository.findExistingOneToOneChatRoom(
                    createChatRoomRequest.targetMemberIds.first(),
                    createChatRoomRequest.targetMemberIds.last()
                )
                    // 존재하면
                    ?.let {
                        chatRoom = it

                        // redis에 채팅방에 속한 회원 정보 조회
                        val chatRoomMemberIds = redisTemplate.opsForSet()
                            .members("$CHAT_ROOM_MEMBERS ${chatRoom!!.id!!}") as Set<Long>

                        chatRoomMemberIds
                            .forEach { chatRoomMemberId ->
                                redisTemplate.opsForHash<String, String>()
                                    .get(MEMBER_ID_TO_SERVER_PORT.toString(), memberId.toString())
                                    ?.split(" ")
                                    ?.get(1)
                                    ?.let { serverPort ->
                                        // 채팅방 생성 이벤트 전송
                                        kafkaTemplate.send(
                                            "$KAFKA_CHAT_SERVER-$serverPort",
                                            objectMapper.writeValueAsString(
                                                KafkaMessage(
                                                    type = CREATE_CHAT_ROOM,
                                                    targetMemberId = chatRoomMemberId,
                                                    data = CreateChatRoomResponse(
                                                        chatRoomId = chatRoom!!.id!!,
                                                        title = chatRoom!!.title,
                                                        memberIds = chatRoomMemberIds,
                                                    )
                                                )
                                            )
                                        )
                                    }
                                    ?: run {
                                        // ToDo: 푸시 알림 요청
                                        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 푸시 알림 요청 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                                    }
                            }

                        // 끝
                        return
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

        // redis에 채팅방 참여 회원 정보 저장
        val key = "$CHAT_ROOM_MEMBERS ${chatRoom!!.id!!}"

        val chatRoomMembers = mutableSetOf<Long>()
        val chatRoomMembersString = mutableSetOf<String>()
        chatRoom!!.memberChatRooms.map {
            chatRoomMembers.add(it.memberId)
            chatRoomMembersString.add(it.memberId.toString())
        }

        // redis에 채팅방 참여 회원 정보 저장
        redisTemplate.opsForSet().add(key, *chatRoomMembersString.toTypedArray())

        // redis에 채팅방 생성 요청
        // 채팅방에 "채팅방이 생성되었습니다." 메시지 추가
        val newMessageId = idGenerator.nextId()
        val newChatMessageRedis = ChatMessageRedis(
            senderId = 0L,
            message = "채팅방이 생성되었습니다."
        )

        val systemResponseMessage = ChatMessageResponse(
            chatId = newMessageId,
            chatRoomId = chatRoom!!.id!!,
            senderId = newChatMessageRedis.senderId,
            message = newChatMessageRedis.message
        )

        // Json으로 변환
        val jsonChatMessage = objectMapper.writeValueAsString(newChatMessageRedis)

        // 채팅방에 메시지 추가
        redisTemplate.opsForZSet().add("$CHAT_MESSAGE_STORAGE ${chatRoom!!.id}", jsonChatMessage, newMessageId.toDouble())

        // 채팅방에 속한 회원들에게 메시지 전송
        chatRoomMembers.forEach { newMemberId ->
            // redis에 회원이 속한 채팅 서버를 조회하고
            redisTemplate.opsForHash<String, String>()
                .get(MEMBER_ID_TO_SERVER_PORT.toString(), newMemberId.toString())
                ?.let {
                    val serverPort = it.split(" ")[1]

                    // 채팅방 생성 이벤트 전송
                    kafkaTemplate.send(
                        "$KAFKA_CHAT_SERVER-$serverPort",
                        objectMapper.writeValueAsString(
                            KafkaMessage(
                                CREATE_CHAT_ROOM,
                                newMemberId,
                                CreateChatRoomResponse(
                                    chatRoomId = chatRoom!!.id!!,
                                    title = chatRoom!!.title,
                                    memberIds = chatRoomMembers,
                                )
                            )
                        )
                    )
                    // 해당 Kafka에 메시지 발행
                    kafkaTemplate.send(
                        "$KAFKA_CHAT_SERVER-$serverPort",
                        objectMapper.writeValueAsString(
                            KafkaMessage(
                                CHAT_MESSAGE,
                                newMemberId,
                                systemResponseMessage
                            )
                        ),
                    )
                }
                ?: run {
                    // ToDo: 아니면 푸시 알림 요청
                    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 푸시 알림 요청 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                }
        }
    }

    private fun sendEventToKafka(
        memberId: Long,
        chatMessage: ChatMessageResponse
    ) {

    }

    @Transactional
    fun deleteChatRoom(memberId: Long, @Valid deleteChatRoomRequest: DeleteChatRoomRequest) {
        // memberId가 들어가있고, chatRoomId가 일치하는 member chat room id 조회
        memberChatRoomRepository.findMemberChatRoomIdByMemberIdAndChatRoomId(memberId, deleteChatRoomRequest.chatRoomId)
            ?: throw IllegalArgumentException("채팅방이 존재하지 않습니다.")

        // member chat room 모두 삭제
        memberChatRoomRepository.deleteByChatRoomId(deleteChatRoomRequest.chatRoomId)

        // chat room 삭제
        chatRoomRepository.deleteById(deleteChatRoomRequest.chatRoomId)

        // redis에서 채팅방 참여 회원 정보를 가져온다.
        val memberIds = redisTemplate.opsForSet()
            .members("$CHAT_ROOM_MEMBERS ${deleteChatRoomRequest.chatRoomId}") as Set<String>

        memberIds.forEach { memberId ->
            // kafka에 채팅방 삭제 이벤트 전송
            val serverPort = redisTemplate.opsForHash<String, String>()
                .get(MEMBER_ID_TO_SERVER_PORT.toString(), memberId)

            serverPort
                ?.let { serverPort ->
                    kafkaTemplate.send(
                        "$KAFKA_CHAT_SERVER-${serverPort.split(" ")[1]}",
                        objectMapper.writeValueAsString(
                            KafkaMessage(
                                DELETE_CHAT_ROOM,
                                memberId.toLong(),
                                DeleteChatRoomResponse(
                                    chatRoomId = deleteChatRoomRequest.chatRoomId
                                )
                            )
                        )
                    )
                }
                ?: run {
                    // ToDo: 푸시 알림 요청
                    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 푸시 알림 요청 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                }
        }

        // redis에서 채팅방 참여 회원 정보 삭제
        redisTemplate.delete("$CHAT_ROOM_MEMBERS ${deleteChatRoomRequest.chatRoomId}")

        // redis에서 채팅방 메시지 삭제
        redisTemplate.delete("$CHAT_MESSAGE_STORAGE ${deleteChatRoomRequest.chatRoomId}")
    }

    private fun createChatRoom(
        createChatRoomRequest: CreateChatRoomRequest,
    ): ChatRoom {
        val chatRoom = ChatRoom(
            title = createChatRoomRequest.title,
            newMemberIds = createChatRoomRequest.targetMemberIds
        )

        return chatRoomRepository.save(chatRoom)
    }
}
